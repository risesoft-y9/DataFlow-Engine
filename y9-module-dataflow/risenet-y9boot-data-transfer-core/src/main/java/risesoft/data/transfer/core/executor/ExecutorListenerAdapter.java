package risesoft.data.transfer.core.executor;

/**
 * 空的监听器用于适配器
 * 
 * @typeName ExecutorListenerAdapter
 * @date 2023年12月11日
 * @author lb
 */
public class ExecutorListenerAdapter implements ExecutorListener {

	@Override
	public void taskEnd(Object task) {
		
	}

	@Override
	public void start() {
		
	}

	@Override
	public void onError(Throwable e) {
		
	}

	@Override
	public void taskStart(Object task) {
		// TODO Auto-generated method stub
		
	}

}
