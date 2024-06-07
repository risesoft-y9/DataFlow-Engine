package risesoft.data.transfer.base.stream.out;

import java.util.List;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.record.Ack;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.core.stream.out.DataOutputStreamFactory;
import risesoft.data.transfer.core.stream.out.OutResult;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ValueUtils;

/**
 * 打印输出的输出流
 * 
 * @typeName PrintOutputStream
 * @date 2023年12月11日
 * @author lb
 */
public class PrintOutputStreamFactory implements DataOutputStreamFactory {

	private String split;

	public PrintOutputStreamFactory(Configuration configuration) {
		split = ValueUtils.getDefault(configuration.getString("split"), ",");
	}

	@Override
	public void init() {
	}

	@Override
	public DataOutputStream getStream() {
		return new DataOutputStream() {

			@Override
			public void close() throws Exception {
System.out.println("关闭输出流");
			}

			@Override
			public void writer(List<Record> records,Ack ack) {
				for (Record record : records) {
					writer(record,ack);
				}
			}

			@Override
			public void writer(Record record,Ack ack) {				
//				for (int i = 0; i < record.getColumnNumber(); i++) {
//					System.out.print(record.getColumn(i).getRawData());
//					if (i != record.getColumnNumber() - 1) {
//						System.out.print(split);
//					}
//				}
//				System.out.println();
				ack.confirm(record);
//				System.out.println("发送异常");
//				throw new RuntimeException("哈哈");
			}
		};
	}

	@Override
	public void close() throws Exception {
	}

}
