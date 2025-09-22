package risesoft.data.transfer.base.queue;

import java.io.IOException;

public interface Serializer<T> {
	byte[] serialize(T item) throws IOException;

	T deserialize(byte[] data) throws IOException;
}