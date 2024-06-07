package net.risedata.jdbc.factory;

import net.risedata.jdbc.builder.OperationBuilder;
import net.risedata.jdbc.operation.Operation;

/**
 * 构建用于创建operationMap的工厂
 * 
 * @author libo 2021年7月6日
 */
public class OperationBuilderFactory {

	
	public static OperationBuilder getInstance() {
		return new OperationBuilder();
	}
	
	public static OperationBuilder builder(String key,Operation value) {
		return new OperationBuilder().builder(key, value);
	}
	
	public static OperationBuilder builder(String key,Operation value,String key2,Operation value2) {
		return new OperationBuilder().builder(key, value).builder(key2, value2);
	}
}
