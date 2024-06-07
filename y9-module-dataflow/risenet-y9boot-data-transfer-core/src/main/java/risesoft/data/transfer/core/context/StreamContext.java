package risesoft.data.transfer.core.context;


import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.core.stream.out.DataOutputStreamFactory;

/**
 * 管理流
 * 
 * @typeName StreamContext
 * @date 2023年12月7日
 * @author lb
 */
public class StreamContext {
	/**
	 * 输入流
	 */
	private DataInputStreamFactory dataInputStream;
	/**
	 * 输出流
	 */
	private DataOutputStreamFactory dataOutputStream;

	public DataInputStreamFactory getDataInputStreamFactory() {
		return dataInputStream;
	}

	public void setDataInputStreamFactory(DataInputStreamFactory dataInputStream) {
		this.dataInputStream = dataInputStream;
	}

	public DataOutputStreamFactory getDataOutputStreamFactory() {
		return dataOutputStream;
	}

	public void setDataOutputStreamFactory(DataOutputStreamFactory dataOutputStream) {
		this.dataOutputStream = dataOutputStream;
	}

}
