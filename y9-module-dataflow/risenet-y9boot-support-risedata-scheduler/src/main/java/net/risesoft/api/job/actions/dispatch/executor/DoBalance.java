package net.risesoft.api.job.actions.dispatch.executor;
import org.springframework.cloud.client.ServiceInstance;

/**
 * @Description : 负载
 * @ClassName DoBalance
 * @Author lb
 * @Date 2023/1/28 10:16
 * @Version 1.0
 */
public interface DoBalance {

    ServiceInstance doBalance(int errorCount,ServiceInstance serviceInstance);
}
