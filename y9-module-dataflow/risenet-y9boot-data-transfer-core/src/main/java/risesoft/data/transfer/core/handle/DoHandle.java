package risesoft.data.transfer.core.handle;



@FunctionalInterface
public interface DoHandle<H> {

	void doHandle(H handles);
}