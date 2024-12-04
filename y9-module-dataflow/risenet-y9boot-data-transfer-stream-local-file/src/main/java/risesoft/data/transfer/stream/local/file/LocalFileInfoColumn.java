package risesoft.data.transfer.stream.local.file;

import risesoft.data.transfer.stream.FileInfoColumn;
import risesoft.data.transfer.stream.Stream;

/**
 * 本地文件信息
 * 
 * 
 * @typeName LocalFileInfoColumn
 * @date 2024年11月27日
 * @author lb
 */
public class LocalFileInfoColumn extends FileInfoColumn {

	public LocalFileInfoColumn(Stream stream, long byteSize, String name, String path, long lastUpdateTime) {
		super(stream, byteSize, name, path, lastUpdateTime);
		
	}

	

}
