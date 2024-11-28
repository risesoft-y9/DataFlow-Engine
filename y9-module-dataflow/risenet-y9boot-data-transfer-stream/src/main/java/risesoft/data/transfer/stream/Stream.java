package risesoft.data.transfer.stream;

import java.io.OutputStream;

/**
 * 流对象
 * 
 * 
 * @typeName Stream
 * @date 2024年11月27日
 * @author lb
 */
public interface Stream {
	/**
	 * 获取流数据
	 * 
	 * @return
	 */
	byte[] getBytes();

	/**
	 * 将数据输出到输出流
	 * 
	 * @param outputStream
	 */
	void writer(OutputStream outputStream);


}
