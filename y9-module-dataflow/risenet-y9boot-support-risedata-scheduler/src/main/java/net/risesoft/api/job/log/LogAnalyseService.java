package net.risesoft.api.job.log;

import net.risesoft.api.persistence.model.log.LogAnalyse;

import java.util.List;
import java.util.Map;

/**
 * @Description : 对日志进行分析的服务接口对调用所有分析服务
 * @ClassName LogAnalyseService
 * @Author lb
 * @Date 2023/11/23 9:49
 * @Version 1.0
 */
public interface LogAnalyseService {
    /**
     * 开始进行分析 map 内包含日志
     * @param jobId 任务id
     * @param log 任务日志
     * @param jobName 任务名
     * @param logAnalyseList
     */
    void doAnalyse(Integer jobId,String log,String jobName, List<LogAnalyse> logAnalyseList,Map<String, Object> map);
}
