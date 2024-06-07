package net.risedata.register.container;

/**
 * @Description : 容器管理器
 * @ClassName ContainerManager
 * @Author lb
 * @Date 2021/12/22 15:20
 * @Version 1.0
 */
public interface ContainerManager {
    /**
     * 重启
     */
    void reStart();

    /**
     * 启动
     */
    void start();

    /**
     * 停止
     */
    void stop();

}
