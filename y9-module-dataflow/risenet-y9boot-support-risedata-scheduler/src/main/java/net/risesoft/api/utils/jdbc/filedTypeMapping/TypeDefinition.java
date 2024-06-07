package net.risesoft.api.utils.jdbc.filedTypeMapping;

public class TypeDefinition {

//字段类型名称
	private String typeName;

//java.sql.types 的映射关系
	private int typeNum;

//字段长度最大值 为0 代表不可填写长度
	private int precision;

	public TypeDefinition(String typeName, int typeNum, int precision) {
		this.typeName = typeName;
		this.typeNum = typeNum;
		this.precision = precision;
	}

	public String getTypeName() {
		return typeName;
	}

	public int getTypeNum() {
		return typeNum;
	}

	public int getPrecision() {
		return precision;
	}
}