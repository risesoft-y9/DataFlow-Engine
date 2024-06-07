package risesoft.data.transfer.core.handle;



/**
 * 空的上下文
 * 
 * @typeName EmptyHandleContext
 * @date 2023年12月6日
 * @author lb
 * @param <F>
 */
@SuppressWarnings("rawtypes")
public class EmptyHandleContext extends HandleContext {

	public static EmptyHandleContext EMPTY = new EmptyHandleContext();

	@Override
	public void handle(DoHandle doHandle) {

	}

}
