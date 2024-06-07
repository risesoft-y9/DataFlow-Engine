package risesoft.data.transfer.stream.rdbms.in.columns;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import risesoft.data.transfer.core.column.Column;

/**
 * 创建 列数据的接口
 * 
 * @typeName CreateColumn
 * @date 2023年12月14日
 * @author lb
 */
public interface CreateColumnHandle {
	/**
	 * 是否被这个handle处理
	 * 
	 * @param type
	 * @return
	 */
	boolean isHandle(int type);

	/**
	 * 获取列数据
	 * 
	 * @param rs       返回值
	 * @param metaData 元数据
	 * @param index    index
	 * @param encoding 编码
	 * @return
	 */
	Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding) throws Exception;


}
