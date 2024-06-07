package net.risedata.rpc.consumer.core;

import java.util.List;

/**
 * 连接池对象
 */
public interface ConnectionPool {
    /**
     * 添加一个连接到连接池中
     *
     * @param connection 需要添加的连接
     */
    void addConnection(Connection connection);

    /**
     * 删除一个连接
     *
     * @param connection 删除的连接
     */
    boolean removeConnection(Connection connection);

    /**
     * 拿到一个连接
     *
     * @return 连接
     */
    Connection getConnection();

    /**
     * 拿到所有的连接
     *
     * @return 当前连接池中所有的连接
     */
    List<Connection> getConnections();

    /**
     * 拿到当前的连接数量
     *
     * @return 当前连接的数量
     */
    int size();
}
