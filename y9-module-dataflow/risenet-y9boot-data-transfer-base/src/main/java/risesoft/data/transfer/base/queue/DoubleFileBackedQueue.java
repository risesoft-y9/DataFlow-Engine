package risesoft.data.transfer.base.queue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import risesoft.data.transfer.core.close.Closed;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.FrameworkErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.util.ByteUtils;

/**
 * 文件临时存储队列，将超过设定值的数据进行临时存储到文件中
 * 
 * 
 * @typeName FileBackedQueue
 * @date 2025年9月2日
 * @author lb
 */
public class DoubleFileBackedQueue<T> implements AutoCloseable, Closed, Queue<T> {
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
	 * 加入操作的锁
	 */
	private final ReentrantLock takeLock = new ReentrantLock();

	/**
	 * 添加操作的锁
	 */
	private final ReentrantLock putLock = new ReentrantLock();

	/**
	 * 存储的文件
	 */
	private final Path storageFile;

	/**
	 * 存储的文件
	 */
	private final Path storageFile2;

	/**
	 * 临时文件NIO 输出通道
	 */
	private FileChannel fileChannelWriter;
	/**
	 * 临时文件NIO 读取通道
	 */
	private FileChannel fileChannelRead;

	private long clearSize = 0;

	/**
	 * 创建临时存储队列
	 * 
	 * @param memoryCacheSize 存储任务的条数
	 * @param storageFileName 存储文件名称
	 * @param serializer      序列化对象
	 * @param fileSize        允许存储的文件大小byte，当数据超过这个值的时候会触发读取后清理的操作
	 * @throws IOException
	 */
	public DoubleFileBackedQueue(int memoryCacheSize, String storageFileName, Serializer<T> serializer, String fileSize)
			throws IOException {
		this.memoryCacheSize = memoryCacheSize;
		this.memoryQueue = new ConcurrentLinkedQueue<>();
		this.serializer = serializer;
		this.storageFile = Paths.get(storageFileName);
		this.storageFile2 = Paths.get(storageFileName + "_temp");
		// 如果文件已存在，先删除（避免旧数据干扰）
		if (Files.exists(storageFile)) {
			Files.delete(storageFile);
		}
		if (Files.exists(storageFile2)) {
			Files.delete(storageFile2);
		}
		maxFileSize = ByteUtils.convertToBytes(fileSize);
		clearSize = (long) (maxFileSize * 0.2);
		this.fileChannelWriter = FileChannel.open(storageFile, StandardOpenOption.CREATE, StandardOpenOption.READ,
				StandardOpenOption.WRITE);
		this.fileChannelRead = FileChannel.open(storageFile2, StandardOpenOption.CREATE, StandardOpenOption.READ,
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
					writeToFile(item);
				} catch (IOException e) {
					throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "队列写入文件异常!", e);
				}
			} else {
				memoryQueue.offer(item);
			}

			return true;
		} finally {
			putLock.unlock();
		}
	}

	public T poll() {
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
		fileChannelWriter.position(fileChannelWriter.size());
		fileChannelWriter.write(buffer);
		fileItemCount.incrementAndGet();
	}

	private void loadBatchFromFile(int batchSize) throws IOException {

		if (fileItemCount.get() == 0)
			return;
		int actualBatchSize = Math.min(batchSize, fileItemCount.get());
		List<T> loadedItems = new ArrayList<>(actualBatchSize);
		fileChannelRead.position(readPosition);
		for (int i = 0; i < actualBatchSize; i++) {
			try {
				if (fileChannelRead.position() == fileChannelRead.size()) {
					cutChannel();
				}
				// 读取长度字段
				ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
				int bytesRead = fileChannelRead.read(lengthBuffer);
				if (bytesRead != 4) {

					throw new IOException("Failed to read item length from file");
				}
				lengthBuffer.flip();
				int dataLength = lengthBuffer.getInt();

				// 读取数据
				ByteBuffer dataBuffer = ByteBuffer.allocate(dataLength);
				bytesRead = fileChannelRead.read(dataBuffer);
				if (bytesRead != dataLength) {
					throw new IOException("Failed to read complete item data from file");
				}
				dataBuffer.flip();

				T item = serializer.deserialize(dataBuffer.array());
				loadedItems.add(item);

			} catch (Exception e) {
				throw new IOException("Failed to load batch from file", e);
			}
		}
		readPosition = fileChannelRead.position();
		memoryQueue.addAll(loadedItems);
		fileItemCount.addAndGet(-actualBatchSize);
		if (fileChannelRead.size() > maxFileSize && readPosition > clearSize) {
			truncateFile(readPosition);
		}

	}

	/**
	 * 指针切换
	 */
	private void cutChannel() {
		lockAll();
		try {
			readPosition = 0;
			FileChannel temp = fileChannelRead;
			// 清空读取文件
			try {
				fileChannelRead.truncate(fileChannelRead.size());
			} catch (IOException e) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "文件清理异常!", e);
			}
			fileChannelRead = fileChannelWriter;
			fileChannelWriter = temp;
			try {
				fileChannelRead.position(0);
			} catch (IOException e) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "文件切换指针异常!", e);
			}
		} finally {
			unLockAll();
		}

	}

	private void truncateFile(long bytesToRemove) throws IOException {
		lockAll();
		try {
			if (fileChannelRead.size() > maxFileSize && readPosition > clearSize) {
				if (bytesToRemove <= 0)
					return;
				long currentSize = fileChannelRead.size();
				if (bytesToRemove >= currentSize) {
					fileChannelRead.truncate(0);
					fileChannelRead.position(0);
					return;
				}
				try (FileChannel tempChannel = FileChannel.open(
						Files.createTempFile("truncate" + storageFile.getFileName(), ".tmp"), StandardOpenOption.READ,
						StandardOpenOption.WRITE, StandardOpenOption.DELETE_ON_CLOSE)) {
					// 将剩余数据拷贝到临时文件（零拷贝优化）
					fileChannelRead.position(bytesToRemove);
					fileChannelRead.transferTo(fileChannelRead.position(), currentSize - bytesToRemove, tempChannel);
					// 重置原文件并写回剩余数据
					fileChannelRead.truncate(0);
					fileChannelRead.position(0);
					tempChannel.position(0);
					tempChannel.transferTo(0, tempChannel.size(), fileChannelRead);
					readPosition = 0;
				}
			}
		} finally {
			unLockAll();
		}

	}

	public void close() throws IOException {
		lockAll();
		if (isClosed)
			return;

		try {
			fileChannelRead.close();
			fileChannelWriter.close();
		} finally {
			try {
				Files.deleteIfExists(storageFile);
				Files.deleteIfExists(storageFile2);
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

	@SuppressWarnings("deprecation")
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

	@Override
	public void clear() {
		lockAll();
		readPosition = 0;
		fileItemCount.set(0);
		memoryQueue.clear();
		try {
			fileChannelRead.truncate(0);
			fileChannelRead.position(0);
			fileChannelWriter.truncate(0);
			fileChannelWriter.position(0);
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

	@SuppressWarnings("hiding")
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