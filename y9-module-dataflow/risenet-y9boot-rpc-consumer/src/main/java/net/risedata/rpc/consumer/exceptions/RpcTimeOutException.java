package net.risedata.rpc.consumer.exceptions;

import net.risedata.rpc.model.Request;

public class RpcTimeOutException extends CarryRequestedException {


    public RpcTimeOutException(String msg, Request request) {
       super(msg,request);
    }



}
