package risesoft.data.transfer.core.statistics;

/**
 * 状态记录类
 * 
 * @typeName State
 * @date 2023年12月4日
 * @author lb
 */
public enum State implements EnumVal {
//提交中
	SUBMITTING(10),
	// 休眠中
	WAITING(20),
	// 执行中
	RUNNING(30),
	// 关闭的
	KILLED(50),
	// 失败了
	FAILED(60),
	// 成功了
	SUCCEEDED(70);

	int value;

	State(int value) {
		this.value = value;
	}

	@Override
	public int value() {
		return value;
	}

	public boolean isFinished() {
		return this == KILLED || this == FAILED || this == SUCCEEDED;
	}

	public boolean isRunning() {
		return !isFinished();
	}

}