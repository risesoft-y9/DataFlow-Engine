package net.risedata.jdbc.executor.table;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.search.Order;
import net.risedata.jdbc.table.TableConfig;

/**
 * 对于表的操作
 * 
 * @author libo 2021年2月18日
 */
public interface TableExecutor {
	public static final String ORACLE = " select count(*) from user_objects where  object_name =  '%s'";
	public static final String MYSQL = "SELECT count(*) FROM information_schema.TABLES WHERE table_name ='%s'";

	/**
	 * 判断一个表是否存在
	 * 
	 * @param tableName 表名
	 * @param findTable 查找的sql 需要带 %s进行format
	 * @return
	 */
	boolean hasTable(@NotNull String tableName,@NotNull String findTable);

	/**
	 * 删除一个表
	 * 
	 * @param tableName tableName 表名
	 * @param findTable 查找的sql 需要带 %s进行format
	 * @return
	 */
	boolean deleteTable(@NotNull String tableName,@NotNull String findTable);

	/**
	 * 修改表名
	 * 
	 * @param ovlTableName 旧名字
	 * @param newName      新名字
	 * @param findTable    查找的sql 需要带 %s进行format
	 * @return
	 */
	boolean updateTableName(@NotNull String ovlTableName, @NotNull String newName, @NotNull String findTable);

	/**
	 * 创建表
	 * 
	 * @param findStr 查找的字符串
	 * @param tc      表的配置
	 * @return
	 */
	boolean createTable(@NotNull String findStr, @NotNull TableConfig tc);

	/**
	 * 通过table config添加到配置池中
	 * 
	 * @param tc         配置的config
	 * @param id         id获取这个beanconfig的id
	 * @param operations 格外的操作的map如果不指定会使用默认的
	 * @param orders     排序
	 */
	void addConfig(@NotNull TableConfig tc, @NotNull String id, Map<String, Operation> operations, List<Order> orders);

	/**
	 * 同一个表的数据转移
	 * 
	 * @param tableName    表名字
	 * @param transferName 转移的表名字
	 * @param keyMapping   key的对应关系
	 * @param where        过滤条件
	 */
	void tableTransfer(@NotNull String tableName, @NotNull String transferName, @NotNull Map<String, String> keyMapping,
			@NotNull String where);
}
