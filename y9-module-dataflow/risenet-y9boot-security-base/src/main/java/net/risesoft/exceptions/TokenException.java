package net.risesoft.exceptions;

/**
 * @Description : token异常
 * @ClassName ServiceOperationException
 * @Author lb
 * @Date 2022/8/4 17:07
 * @Version 1.0
 */
public class TokenException extends RuntimeException {

	private static final long serialVersionUID = 5338676510040885569L;

	public TokenException(String msg){
        super(msg);
    }
}
