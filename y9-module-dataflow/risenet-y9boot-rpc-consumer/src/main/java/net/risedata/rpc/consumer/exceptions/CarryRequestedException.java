package net.risedata.rpc.consumer.exceptions;


import net.risedata.rpc.model.Request;

public class CarryRequestedException extends RuntimeException{

    private Request request;

    public CarryRequestedException(String msg, Request request) {
        super(msg);
        this.request = request;

    }

    public Request getRequest() {
        return request;
    }
}
