package net.risesoft.api.exceptions;

/**
 * @Description : token异常
 * @ClassName ServiceOperationException
 * @Author lb
 * @Date 2022/8/4 17:07
 * @Version 1.0
 */
public class TokenException extends  RuntimeException {

    public TokenException(String msg){
        super(msg);
    }
}
