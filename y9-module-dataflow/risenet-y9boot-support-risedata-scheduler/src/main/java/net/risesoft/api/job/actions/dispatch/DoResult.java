package net.risesoft.api.job.actions.dispatch;

import net.risesoft.api.job.actions.dispatch.executor.Result;

/**
 * @Description :
 * @ClassName DoResult
 * @Author lb
 * @Date 2022/10/17 10:02
 * @Version 1.0
 */
public interface DoResult {

    Result doResult(int count,Throwable e);
}
