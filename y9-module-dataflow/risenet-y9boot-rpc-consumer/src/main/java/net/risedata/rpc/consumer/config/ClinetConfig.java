package net.risedata.rpc.consumer.config;

import org.springframework.beans.factory.annotation.Value;

/**
 * @description: 客户端配置
 * @Author lb176
 * @Date 2021/4/29==16:05
 */
public class ClinetConfig {

    @Value("${rpc.refresh.time:10000}")
    public Long refreshTime;
    @Value("${rpc.refresh.showTaskInfo:false}")
    public boolean showTaskInfo = false;

    /**
     * 具体具体业务的线程池大小是work线程交由任务的执行线程 <br/>
     * 如果服务器需要执行多种业务则将此线程池的数量调整大一点
     */
    @Value("${rpc.executorSize:20}")
    public int executorSize;

}
