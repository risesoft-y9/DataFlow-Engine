package risesoft.data.transfer.base.queue;

import java.nio.ByteBuffer;

public class IntSerializer implements Serializer<Integer> {
	@Override
	public byte[] serialize(Integer item) {
		ByteBuffer buffer = ByteBuffer.allocate(4);

		buffer.putInt(item);
		return buffer.array();
	}

	@Override
	public Integer deserialize(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		return buffer.getInt();
	}
}