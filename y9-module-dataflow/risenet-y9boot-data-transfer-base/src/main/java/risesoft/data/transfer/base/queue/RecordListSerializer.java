package risesoft.data.transfer.base.queue;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.BoolColumn;
import risesoft.data.transfer.core.column.impl.BytesColumn;
import risesoft.data.transfer.core.column.impl.DateColumn;
import risesoft.data.transfer.core.column.impl.DoubleColumn;
import risesoft.data.transfer.core.column.impl.LongColumn;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.record.DefaultRecord;
import risesoft.data.transfer.core.record.Record;

import com.esotericsoftware.kryo.io.Input;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * record 序列化
 * 
 * 
 * @typeName RecordSerializer
 * @date 2025年9月19日
 * @author lb
 */
public class RecordListSerializer implements Serializer<List<Record>> {
	private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
		Kryo kryo = new Kryo();
		kryo.setRegistrationRequired(false); // 保持强制注册
		kryo.register(DefaultRecord.class);
		kryo.register(Column.Type.class);
		kryo.register(BigInteger.class);
		
		kryo.register(ArrayList.class); // 注册JDK集合类
		kryo.register(BoolColumn.class);
		kryo.register(StringColumn.class);
		kryo.register(BytesColumn.class);
		kryo.register(DateColumn.class);
		kryo.register(DoubleColumn.class);
		kryo.register(LongColumn.class);
		
		kryo.setReferences(true);
		return kryo;
	});

	@Override
	public byte[] serialize(List<Record> items) throws IOException {
		Kryo kryo = kryoThreadLocal.get();
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try (Output output = new Output(byteStream)) {
			kryo.writeObject(output, items);
		}
		return byteStream.toByteArray();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Record> deserialize(byte[] data) throws IOException {
		Kryo kryo = kryoThreadLocal.get();
		return kryo.readObject(new Input(data), ArrayList.class);
	}
}