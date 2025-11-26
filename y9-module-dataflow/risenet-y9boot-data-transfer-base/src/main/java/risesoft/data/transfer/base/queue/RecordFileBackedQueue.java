package risesoft.data.transfer.base.queue;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.close.Closed;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.record.Record;

/**
 * record 文件队列
 * 
 * @typeName RecordFileBackedQueue
 * @date 2025年9月22日
 * @author lb
 */
public class RecordFileBackedQueue implements Queue<List<Record>> ,Closed {

	private FileBackedQueue<List<Record>> fileBackedQueue;
    private Logger logger;
	public RecordFileBackedQueue(@ConfigParameter(description = "缓存队列大小", value = "10") int memoryCacheSize,
			@ConfigParameter(description = "文件名称", required = false) String fileName,
			@ConfigParameter(description = "文件大小", value = "100MB") String fileSize, JobContext jobContext) {
		if (StringUtils.isEmpty(fileName)) {
			fileName = "fileQueue_" + jobContext.getJobId() + ".queue";
		}
		logger = jobContext.getLoggerFactory().getLogger("RecordFileBackedQueue");
		try {
			fileBackedQueue = new FileBackedQueue<List<Record>>(memoryCacheSize, fileName, new RecordListSerializer(),
					fileSize);
		} catch (IOException e) {
			throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "创建文件队列失败!", e);
		}
	}

	@Override
	public int size() {
		return fileBackedQueue.size();
	}

	@Override
	public boolean isEmpty() {
		return fileBackedQueue.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return fileBackedQueue.contains(o);
	}

	@Override
	public Iterator<List<Record>> iterator() {
		return fileBackedQueue.iterator();
	}

	@Override
	public Object[] toArray() {
		return fileBackedQueue.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return fileBackedQueue.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return fileBackedQueue.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return fileBackedQueue.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends List<Record>> c) {
		return fileBackedQueue.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return fileBackedQueue.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return fileBackedQueue.retainAll(c);
	}

	@Override
	public void clear() {
		fileBackedQueue.clear();
	}

	@Override
	public boolean add(List<Record> e) {
		return fileBackedQueue.add(e);
	}

	@Override
	public boolean offer(List<Record> e) {
		return fileBackedQueue.offer(e);
	}

	@Override
	public List<Record> remove() {
		return fileBackedQueue.remove();
	}

	@Override
	public List<Record> poll() {
		return fileBackedQueue.poll();
	}

	@Override
	public List<Record> element() {
		return fileBackedQueue.element();
	}

	@Override
	public List<Record> peek() {
		return fileBackedQueue.peek();
	}

	@Override
	public void close() throws Exception {
		logger.debug(this,"recordFileBackedQueue close ");
		fileBackedQueue.close();
		logger.debug(this,"recordFileBackedQueue closed");
	}

}
