package net.risedata.jdbc.operation;
/**
 * 数据库字段类型订单默认operation接口
 */
public interface SqlDefaultOperation {
    public boolean hasOperation(String sqlType);
    public Operation getOperation(String sqlType);
}
