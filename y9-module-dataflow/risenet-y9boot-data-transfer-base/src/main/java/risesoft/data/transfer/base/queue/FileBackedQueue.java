package risesoft.data.transfer.base.queue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import risesoft.data.transfer.core.close.Closed;
import risesoft.data.transfer.core.column.impl.LongColumn;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.exception.FrameworkErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.record.DefaultRecord;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.util.ByteUtils;
import risesoft.data.transfer.core.util.pool.BlockQueue;

/**
 * 文件临时存储队列，将超过设定值的数据进行临时存储到文件中
 * 
 * 
 * @typeName FileBackedQueue
 * @date 2025年9月2日
 * @author lb
 */
public class FileBackedQueue<T> implements AutoCloseable, Closed, Queue<T> {
	/**
	 * 内存存储的任务条数
	 */
	private final int memoryCacheSize;
	/**
	 * 内存存储queue
	 */
	private final Queue<T> memoryQueue;

	/**
	 * 序列化数据接口
	 */
	private final Serializer<T> serializer;
	/**
	 * 存储在文件中的数据数量
	 */
	private AtomicInteger fileItemCount = new AtomicInteger();
	/**
	 * 是否关闭
	 */
	private boolean isClosed = false;

	/**
	 * 读取的位置
	 */
	private long readPosition = 0;

	private long maxFileSize = 0;
	/**
	 * 文件操作
	 */
	private ReentrantLock fileLock = new ReentrantLock();
	/**
	 * 添加操作
	 */
	private ReentrantLock putLock = new ReentrantLock();

	/**
	 * 移除操作
	 */
	private ReentrantLock takeLock = new ReentrantLock();

	/**
	 * 存储的文件
	 */
	private final Path storageFile;

	/**
	 * 临时文件NIO 输出通道
	 */
	private final FileChannel fileChannel;

	private long clearSize;

	/**
	 * 创建临时存储队列
	 * 
	 * @param memoryCacheSize 存储任务的条数
	 * @param storageFileName 存储文件名称
	 * @param serializer      序列化对象
	 * @param fileSize        允许存储的文件大小byte，当数据超过这个值的时候会触发读取后清理的操作
	 * @throws IOException
	 */
	public FileBackedQueue(int memoryCacheSize, String storageFileName, Serializer<T> serializer, String fileSize)
			throws IOException {
		this.memoryCacheSize = memoryCacheSize;
		this.memoryQueue = new ConcurrentLinkedQueue<>();
		this.serializer = serializer;
		this.storageFile = Paths.get(storageFileName);
		// 如果文件已存在，先删除（避免旧数据干扰）
		if (Files.exists(storageFile)) {
			Files.delete(storageFile);
		}
		maxFileSize = ByteUtils.convertToBytes(fileSize);
		clearSize = (long) (maxFileSize * 0.2);
		this.fileChannel = FileChannel.open(storageFile, StandardOpenOption.CREATE, StandardOpenOption.READ,
				StandardOpenOption.WRITE);
	}

	public boolean add(T item) {
		putLock.lock();
		try {

			if (isClosed) {
				throw new IllegalStateException("Queue is closed");
			}
			if (memoryQueue.size() >= memoryCacheSize) {
				try {
					fileLock.lock();
					try {
						writeToFile(item);
					} finally {
						fileLock.unlock();
					}

				} catch (IOException e) {
					throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "队列写入文件异常!", e);
				}
			} else {
				memoryQueue.offer(item);
			}
		} finally {
			putLock.unlock();
		}
		return true;
	}

	public T poll() {
		takeLock.lock();
		try {

			if (isClosed) {
				throw new IllegalStateException("Queue is closed");
			}
			if (memoryQueue.isEmpty() && fileItemCount.get() > 0) {
				fileLock.lock();
				try {
					loadBatchFromFile(memoryCacheSize);
				} catch (IOException e) {
					throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "队列读取文件异常!", e);
				} finally {
					fileLock.unlock();
				}
			}
			if (memoryQueue.isEmpty()) {
				throw new NoSuchElementException("Queue is empty");
			}

			return memoryQueue.poll();
		} finally {
			takeLock.unlock();
		}
	}

	public T peek() {
		takeLock.lock();
		try {

			if (isClosed) {
				throw new IllegalStateException("Queue is closed");
			}

			if (memoryQueue.isEmpty() && fileItemCount.get() > 0) {
				try {
					loadBatchFromFile(memoryCacheSize);
				} catch (IOException e) {
					throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "队列读取文件异常!", e);
				}
			}

			if (memoryQueue.isEmpty()) {
				throw new NoSuchElementException("Queue is empty");
			}

			return memoryQueue.peek();
		} finally {
			takeLock.unlock();
		}
	}

	public int size() {
		return memoryQueue.size() + fileItemCount.get();

	}

	public boolean isEmpty() {
		return memoryQueue.isEmpty() && fileItemCount.get() == 0;

	}

	private void writeToFile(T item) throws IOException {
		byte[] data = serializer.serialize(item);
		ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
		buffer.putInt(data.length);
		buffer.put(data);
		buffer.flip();
		fileChannel.position(fileChannel.size());
		fileChannel.write(buffer);
		fileItemCount.incrementAndGet();
	}

	private void loadBatchFromFile(int batchSize) throws IOException {
		if (fileItemCount.get() == 0)
			return;
		int actualBatchSize = Math.min(batchSize, fileItemCount.get());
		List<T> loadedItems = new ArrayList<>(actualBatchSize);
		fileChannel.position(readPosition);
		for (int i = 0; i < actualBatchSize; i++) {
			try {
				// 读取长度字段
				ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
				int bytesRead = fileChannel.read(lengthBuffer);
				if (bytesRead != 4) {
					throw new IOException("Failed to read item length from file");
				}
				lengthBuffer.flip();
				int dataLength = lengthBuffer.getInt();

				// 读取数据
				ByteBuffer dataBuffer = ByteBuffer.allocate(dataLength);
				bytesRead = fileChannel.read(dataBuffer);
				if (bytesRead != dataLength) {
					throw new IOException("Failed to read complete item data from file");
				}
				dataBuffer.flip();

				T item = serializer.deserialize(dataBuffer.array());
				loadedItems.add(item);
			} catch (Exception e) {
				// 清理部分读取的数据
				throw new IOException("Failed to load batch from file", e);
			}
		}
		readPosition = fileChannel.position();
		memoryQueue.addAll(loadedItems);
		fileItemCount.addAndGet(-actualBatchSize);
		if (fileChannel.size() > maxFileSize && readPosition > clearSize) {
			truncateFile(readPosition);
		}

	}

	private void truncateFile(long bytesToRemove) throws IOException {
		if (bytesToRemove <= 0)
			return;

		long currentSize = fileChannel.size();
		if (bytesToRemove >= currentSize) {
			fileChannel.truncate(0);
			fileChannel.position(0);
			return;
		}
		try (FileChannel tempChannel = FileChannel.open(
				Files.createTempFile("truncate" + storageFile.getFileName(), ".tmp"), StandardOpenOption.READ,
				StandardOpenOption.WRITE, StandardOpenOption.DELETE_ON_CLOSE)) {

			// 将剩余数据拷贝到临时文件（零拷贝优化）
			fileChannel.position(bytesToRemove);
			fileChannel.transferTo(fileChannel.position(), currentSize - bytesToRemove, tempChannel);
			// 重置原文件并写回剩余数据
			fileChannel.truncate(0);
			fileChannel.position(0);
			tempChannel.position(0);
			tempChannel.transferTo(0, tempChannel.size(), fileChannel);
			readPosition = 0;
		}
	}

	public void close() throws IOException {
		lockAll();
		if (isClosed)
			return;

		try {
			fileChannel.close();
		} finally {
			try {
				Files.deleteIfExists(storageFile);
			} catch (IOException e) {
				// 记录警告但不抛出
				System.err.println("Warning: Failed to delete storage file: " + e.getMessage());
			}
			isClosed = true;
			unLockAll();
		}

	}

	private void lockAll() {
		putLock.lock();
		takeLock.lock();
	}

	private void unLockAll() {
		putLock.unlock();
		takeLock.unlock();
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			close();
		} finally {
			super.finalize();
		}
	}

	public static class StringSerializer implements Serializer<String> {
		@Override
		public byte[] serialize(String item) {
			return item.getBytes(java.nio.charset.StandardCharsets.UTF_8);
		}

		@Override
		public String deserialize(byte[] data) {
			return new String(data, java.nio.charset.StandardCharsets.UTF_8);
		}
	}

	public static void main(String[] args) throws InterruptedException {
//、双锁，filecount使用juc中的类
		Map<Integer, Object> intMap = new ConcurrentHashMap<Integer, Object>();
		long startTime = System.currentTimeMillis();
		AtomicInteger count = new AtomicInteger();
		try (FileBackedQueue<List<Record>> queue = new FileBackedQueue<>(10, "queue_data.bin", new RecordListSerializer(),
				"1MB")) {
			// 添加项目
			for (int i = 0; i < 10; i++) {
				new Thread(() -> {
					for (int j = 0; j < 100; j++) {
						Record deRecord = new DefaultRecord();
						deRecord.addColumn(new StringColumn("test" + j, "UP"));
						deRecord.addColumn(new LongColumn(count.incrementAndGet(), "count"));
						queue.add(Arrays.asList(deRecord));

						try {
							// 延迟推送
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				}).start();
			}

			System.out.println("Queue size: " + queue.size());
			// 取出项目
			AtomicInteger i = new AtomicInteger();
			CountDownLatch maxSize = new CountDownLatch(10 * 100);
			for (int j = 0; j < 5; j++) {
				while (i.get() < 10 * 100) {
					Thread.sleep(5L);
					if (!queue.isEmpty()) {
						queue.poll();
						i.incrementAndGet(); 
						maxSize.countDown();

					}

				}
			}

			maxSize.await();
			System.out.println(i);
			System.out.println(intMap.size());
			System.out.println((System.currentTimeMillis() - startTime) / 1000 + "s");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clear() {
		lockAll();
		readPosition = 0;
		fileItemCount.set(0);
		memoryQueue.clear();
		try {
			fileChannel.truncate(0);
			fileChannel.position(0);
		} catch (IOException e) {
			throw new RuntimeException("文件清理出错!" + e.getMessage());
		} finally {
			unLockAll();
		}

	}

	@Override
	public boolean contains(Object o) {
		throw new RuntimeException("文件缓存队列无法进行contains判断");

	}

	@Override
	public Iterator<T> iterator() {

		throw new RuntimeException("文件缓存队列无法返回迭代器");

	}

	@Override
	public Object[] toArray() {
		throw new RuntimeException("文件缓存队列不支持此操作");
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new RuntimeException("文件缓存队列不支持此操作");
	}

	@Override
	public boolean remove(Object o) {
		throw new RuntimeException("文件缓存队列不支持此操作");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new RuntimeException("文件缓存队列不支持此操作");
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for (T t : c) {
			add(t);
		}
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new RuntimeException("文件缓存队列不支持此操作");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new RuntimeException("文件缓存队列不支持此操作");
	}

	@Override
	public boolean offer(T e) {
		throw new RuntimeException("文件缓存队列不支持此操作");
	}

	@Override
	public T remove() {
		throw new RuntimeException("文件缓存队列不支持此操作");
	}

	@Override
	public T element() {
		throw new RuntimeException("文件缓存队列不支持此操作");
	}
}