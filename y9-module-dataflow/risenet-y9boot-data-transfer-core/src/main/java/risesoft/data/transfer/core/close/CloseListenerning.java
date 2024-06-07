package risesoft.data.transfer.core.close;

/**
 * 可被监听关闭事件的
 * 
 * @typeName CloseListenerning
 * @date 2023年12月8日
 * @author lb
 */
public interface CloseListenerning {

	void addCloseListener(CloseListener closeListener);
}
