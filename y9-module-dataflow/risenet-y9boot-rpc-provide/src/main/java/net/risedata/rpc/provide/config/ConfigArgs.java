package net.risedata.rpc.provide.config;

import org.springframework.beans.factory.annotation.Value;

/**
 * @description: 参数
 * @Author lb176
 * @Date 2021/4/29==16:28
 */
public class ConfigArgs {
    /**
     * 服务端口
     */
    @Value("${rpc.port:8999}")
    public int port;
    /**
     * 工作线程<br/>
     * ps:这些线程只是netty工作的线程数量这些线程只用于维护连接,
     * 读取数据,交给执行器执行,不负责执行具体业务无需给太多
     */
    @Value("${rpc.workSize:0}")
    public int workSize;
    /**
     * 具体具体业务的线程池大小是work线程交由任务的执行线程 <br/>
     * 如果服务器需要执行多种业务则将此线程池的数量调整大一点
     */
    @Value("${rpc.executorSize:20}")
    public int executorSize;

    /**
     * 是否启动map的匹配方式
     */
    @Value("${rpc.runMap:false}")
    public boolean runAbleMap;
}
