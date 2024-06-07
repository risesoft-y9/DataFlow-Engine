package net.risedata.rpc.consumer.core;


public interface ConnectionManager extends BasedConnectionManager {

    Connection getConnection();
    /**
     * 返回当前连接管理器的id这是每个管理器的唯一标识
     * @return
     */
    int getId();
}
