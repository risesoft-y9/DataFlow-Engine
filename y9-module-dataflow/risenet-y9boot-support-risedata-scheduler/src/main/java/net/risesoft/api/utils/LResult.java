package net.risesoft.api.utils;

import net.risesoft.api.job.actions.dispatch.executor.ResultSuccess;

public class LResult {

    private ResultSuccess resultSuccess;

    private Object[] res;

    public LResult end(Object[] res) {
        this.res = res;
        if (this.resultSuccess != null) {
            this.resultSuccess.onSuccess(res);
        }
        return this;
    }

    public LResult onEnd(ResultSuccess resultSuccess) {
        this.resultSuccess = resultSuccess;
        if (this.res != null) {
            this.resultSuccess.onSuccess(res);
        }
        return this;
    }

}