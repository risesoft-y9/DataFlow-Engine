package net.risesoft.exceptions;

/**
 * @Description : 服务操作出现异常抛出错误
 * @ClassName ServiceOperationException
 * @Author lb
 * @Date 2022/8/4 17:07
 * @Version 1.0
 */
public class ServiceOperationException extends RuntimeException {

	private static final long serialVersionUID = -8820877211760484344L;

	public ServiceOperationException(String msg){
        super(msg);
    }
}
