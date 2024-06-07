package net.risedata.rpc.consumer.exceptions;


import net.risedata.rpc.model.Request;

public class RpcException extends CarryRequestedException {


    public RpcException(String msg) {
        super(msg,null);
    }
    public RpcException(String msg, Request request) {
        super(msg,request);
    }

}
