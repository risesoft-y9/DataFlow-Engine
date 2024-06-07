package risesoft.data.transfer.base.stream.in;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import risesoft.data.transfer.core.channel.InChannel;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.data.ListConfigurationData;
import risesoft.data.transfer.core.record.DefaultRecord;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ValueUtils;

/**
 * 直接读取配置的输入流 { datas:[{ key:value },{ key:value }] }
 * 
 * @typeName LocalReadInputStream
 * @date 2023年12月11日
 * @author lb
 */
public class LocalReadInputStreamFactory implements DataInputStreamFactory {

	private List<Configuration> datas;

	public LocalReadInputStreamFactory(Configuration configuration) {
		datas = ValueUtils.getRequired(configuration.getListConfiguration("datas"), "没有数据");
	}

	@Override
	public void init() {

	}

	@Override
	public DataInputStream getStream() {
		return new DataInputStream() {
			@Override
			public void close() throws Exception {
			}

//弄一个地方收集脏数据考虑以何种方式收集
			@Override
			public void read(Data data, InChannel inChannel) {
				List<Configuration> lists = ((ListConfigurationData) data).getConfigurations();
				for (Configuration configuration : lists) {
					Set<String> keys = configuration.getKeys();
					DefaultRecord defaultRecord = new DefaultRecord();
					try {
						for (String key : keys) {
							defaultRecord.addColumn(new StringColumn(configuration.get(key).toString(), key));
						}
						inChannel.writer(defaultRecord);
					} catch (Exception e) {
						inChannel.collectDirtyRecord(defaultRecord, e, e.getMessage());
					}
				}
				inChannel.flush();
			}
		};
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public List<Data> splitToData(int size) {
		List<Data> datas = new ArrayList<Data>();
		for (int i = 0; i * 2 < this.datas.size(); i++) {
			datas.add((Data) new ListConfigurationData(
					this.datas.subList(i * 2, i * 2 + 2 > this.datas.size() ? this.datas.size() : i * 2 + 2)));
		}
		return datas;
	}

}
