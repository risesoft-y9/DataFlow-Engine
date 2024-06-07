package net.risedata.rpc.consumer.listener.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.risedata.rpc.consumer.core.HostAndPortConnection;
import net.risedata.rpc.consumer.listener.ConnectionListener;

public class LinkedConnectionListener implements ConnectionListener {
    private List<ConnectionListener> listeners = new LinkedList<>();

    public void addListener(ConnectionListener listener) {
        listeners.add(listener);
    }


    public void addListener(Collection<ConnectionListener> listeners){
        listeners.addAll(listeners);
    }

    @Override
    public void onConnection(HostAndPortConnection connection) {
        if (listeners.size()>0){
            for (ConnectionListener listener : listeners) {
                listener.onConnection(connection);
            }
        }
    }

    @Override
    public void onClose(HostAndPortConnection connection) {
        if (listeners.size()>0){
            for (ConnectionListener listener : listeners) {
                listener.onClose(connection);
            }
        }

    }
}
