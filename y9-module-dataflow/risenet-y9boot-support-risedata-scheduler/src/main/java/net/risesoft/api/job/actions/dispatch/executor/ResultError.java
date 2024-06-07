package net.risesoft.api.job.actions.dispatch.executor;

/**
 * @Description : 失败返回值
 * @ClassName Result
 * @Author lb
 * @Date 2022/9/15 11:32
 * @Version 1.0
 */
public interface ResultError {
    /**
     * 成功
     * @param throwable
     * @return
     */
    void onError(Throwable throwable);



}
