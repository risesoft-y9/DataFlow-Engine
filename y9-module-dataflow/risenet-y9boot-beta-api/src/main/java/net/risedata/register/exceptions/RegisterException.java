package net.risedata.register.exceptions;

/**
 * @Description : 关于注册中心的异常
 * @ClassName RegisterException
 * @Author lb
 * @Date 2021/12/6 10:24
 * @Version 1.0
 */
public class RegisterException extends  RuntimeException {

    public RegisterException(String msg){
        super(msg);
    }

}
