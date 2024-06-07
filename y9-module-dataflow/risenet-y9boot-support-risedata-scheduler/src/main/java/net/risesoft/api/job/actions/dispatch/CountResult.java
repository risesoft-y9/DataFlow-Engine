package net.risesoft.api.job.actions.dispatch;

import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.job.actions.dispatch.executor.ResultError;
import net.risesoft.api.job.actions.dispatch.executor.ResultSuccess;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description : 失败重试次数的result
 * @ClassName CountResult
 * @Author lb
 * @Date 2022/10/17 9:45
 * @Version 1.0
 */
public class CountResult implements Result {
	private Result result;
	private List<ResultSuccess> successes = new ArrayList<>();
	private List<ResultError> errors = new ArrayList<>();

	private int count;

	private DoResult doResult;

	private Object val;

	public CountResult() {

	}

	public CountResult(int count, DoResult doResult) {
		this.count = count;
		this.doResult = doResult;
		this.setResult(doResult.doResult(count--, null));
	}

	private void setResult(Result result) {
		this.result = result;
		Object lockObject = this;
		result.onSuccess((val) -> {
			synchronized (lockObject) {
				this.val = val;
				for (int i = 0; i < successes.size(); i++) {
					successes.remove(i).onSuccess(val);
					i--;
				}
			}
		});
		result.onError((err) -> {
			synchronized (lockObject) {
				err.printStackTrace();
				if (count <= 0) {
					onError(err);
				} else {
					count--;
					try {
						setResult(doResult.doResult(count, err));
					} catch (Exception e) {
						onError(e);
					}

				}
			}
		});
	}
//TODO 此处需要优化
	private void onError(Throwable e) {
		for (int i = 0; i < errors.size(); i++) {
			errors.remove(i).onError(e);
			i--;
		}
	}

	@Override
	public synchronized Result onSuccess(ResultSuccess success) {
		if (val != null) {
			success.onSuccess(val);
			return this;
		}
		successes.add(success);
		return this;
	}

	@Override
	public synchronized Result onError(ResultError error) {
		errors.add(error);
		return this;
	}

	@Override
	public Object getValue() {
		throw new JobException("no getValue");
	}
}
