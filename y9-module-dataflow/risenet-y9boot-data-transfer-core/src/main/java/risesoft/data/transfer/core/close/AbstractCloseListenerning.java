package risesoft.data.transfer.core.close;

import java.util.ArrayList;
import java.util.List;

import risesoft.data.transfer.core.util.CloseUtils;

public abstract class AbstractCloseListenerning implements CloseListenerning, Closed {

	private List<CloseListener> listeners = new ArrayList<CloseListener>();

	@Override
	public void close() throws Exception {
		for (CloseListener closeListener : listeners) {
			CloseUtils.close(closeListener);
		}
		this.closed();
	}

	protected abstract void closed();

	@Override
	public void addCloseListener(CloseListener closeListener) {
		listeners.add(closeListener);
	}

}
