package net.risesoft.api.job.actions.dispatch.method;

import net.risedata.register.discover.IServiceInstanceImpl;
import net.risedata.register.service.IServiceInstance;
import net.risedata.register.service.IServiceInstanceFactory;
import net.risesoft.api.api.RegisterApi;
import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.dispatch.DispatchJobAction;
import net.risesoft.api.job.actions.dispatch.executor.ExecutorActionManager;
import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.persistence.model.IServiceInstanceModel;
import net.risesoft.api.persistence.model.job.Job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description :
 * @ClassName AbstractDispatchAction
 * @Author lb
 * @Date 2022/9/14 17:56
 * @Version 1.0
 */
public abstract class AbstractDispatchAction implements DispatchJobAction {
    @Autowired
    public ExecutorActionManager executorActionManager;
    @Autowired
    public DiscoveryClient discoveryClient;

    @Autowired
    IServiceInstanceFactory iServiceInstanceFactory;

    @Autowired
    RegisterApi registerApi;

    protected List<ServiceInstance> getService(Job job) {
        //如果环境和当前环境相符合则调度当前环境
        List<ServiceInstance> instances = null;
        if (iServiceInstanceFactory.getIsntance().getEnvironment().equals(job.getEnvironment())) {
            instances = discoveryClient.getInstances(job.getServiceId());
        } else {
            List<IServiceInstanceModel> iServiceInstanceModels = registerApi.getServices(job.getEnvironment()).get(job.getService());
            if (iServiceInstanceModels != null) {
                instances = new ArrayList<>();
                IServiceInstance iServiceInstance;
                IServiceInstanceModel iServiceInstanceModel;
                for (int i = 0; i < iServiceInstanceModels.size(); i++) {
                    iServiceInstanceModel = iServiceInstanceModels.get(i);
                    iServiceInstance = new IServiceInstance();
                    iServiceInstance.setServiceId(iServiceInstanceModel.getServiceId());
                    iServiceInstance.setWeight(iServiceInstanceModel.getWeight());
                    iServiceInstance.setHost(iServiceInstanceModel.getHost());
                    iServiceInstance.setStatus(iServiceInstanceModel.getStatus());
                    iServiceInstance.setScheme(iServiceInstanceModel.getScheme());
                    iServiceInstance.setPort(iServiceInstanceModel.getPort());
                    iServiceInstance.setEnvironment(iServiceInstanceModel.getEnvironment());
                    iServiceInstance.setContext(iServiceInstanceModel.getContext());
                    iServiceInstance.setInstanceId(iServiceInstanceModel.getInstanceId());
                    iServiceInstance.setSecure(iServiceInstanceModel.getSecure());
                    iServiceInstance.setManagerInfo(iServiceInstanceModel.getManagerInfo());
                    instances.add(new IServiceInstanceImpl(iServiceInstance));
                }
            }

        }
        if (instances == null || instances.isEmpty()) {
            throw new JobException(job.getServiceId() + " 调度失败:服务不存在实例!");
        }
        return instances;
    }

    protected Result onResult(String jobId, Map<String, Object> res, TaskExecutorService taskExecutor, ServiceInstance instance, Result result) {

       return  result.onSuccess((object) -> {
            taskExecutor.appendLog(jobId, instance.getInstanceId() + "调度成功返回值:" + object);
            res.put(instance.getInstanceId(), object);
        }).onError((e) -> {
            res.put(instance.getInstanceId(), "error:" + e.getMessage());
            taskExecutor.appendLog(jobId, instance.getInstanceId() + "调度失败原因:" + e.getMessage());
        });
    }
}
