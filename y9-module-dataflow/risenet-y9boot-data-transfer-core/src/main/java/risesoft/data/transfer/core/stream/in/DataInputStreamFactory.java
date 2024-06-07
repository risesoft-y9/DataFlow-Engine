package risesoft.data.transfer.core.stream.in;

import java.util.List;


import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.stream.DataStreamFactory;

/**
 * 数据输入流
 * 
 * @typeName DataInputStream
 * @date 2023年12月4日
 * @author lb
 */
public interface DataInputStreamFactory extends DataStreamFactory<DataInputStream> {

	/**
	 * 切分为数据对象
	 * 
	 * @return
	 */
	List<Data> splitToData(int executorSize)  throws Exception ;

}
