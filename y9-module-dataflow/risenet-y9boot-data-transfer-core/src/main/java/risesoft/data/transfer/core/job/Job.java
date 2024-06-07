package risesoft.data.transfer.core.job;

import java.util.List;

import risesoft.data.transfer.core.context.StreamContext;
import risesoft.data.transfer.core.data.Data;

/**
 * 任务对象包含一个任务需要执行的对象
 * 
 * @typeName Job
 * @date 2023年12月7日
 * @author lb
 */
public class Job {
	/**
	 * 任务的流信息
	 */
	private StreamContext streamContext;
	/**
	 * 这个任务需要抽取的数据
	 */
	private List<Data> datas;

	public StreamContext getStreamContext() {
		return streamContext;
	}

	public void setStreamContext(StreamContext streamContext) {
		this.streamContext = streamContext;
	}

	public List<Data> getDatas() {
		return datas;
	}

	public void setDatas(List<Data> datas) {
		this.datas = datas;
	}

}
