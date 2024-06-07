package risesoft.data.transfer.core.exception;


import java.io.PrintWriter;
import java.io.StringWriter;
/**
 * 程序通用异常
 * @typeName TransferException
 * @date 2023年12月4日
 * @author lb
 */
public class TransferException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode;
    public TransferException( String errorMessage) {
        super( errorMessage);
    }
    public TransferException(ErrorCode errorCode, String errorMessage) {
        super(errorCode.toString() + " - " + errorMessage);
        this.errorCode = errorCode;
    }

    private TransferException(ErrorCode errorCode, String errorMessage, Throwable cause) {
        super(errorCode.toString() + " - " + getMessage(errorMessage) + " - " + getMessage(cause), cause);

        this.errorCode = errorCode;
    }

    public static TransferException as(ErrorCode errorCode, String message) {
        return new TransferException(errorCode, message);
    }

    public static TransferException as(ErrorCode errorCode, String message, Throwable cause) {
        if (cause instanceof TransferException) {
            return (TransferException) cause;
        }
        return new TransferException(errorCode, message, cause);
    }

    public static TransferException as(ErrorCode errorCode, Throwable cause) {
        if (cause instanceof TransferException) {
            return (TransferException) cause;
        }
        return new TransferException(errorCode, getMessage(cause), cause);
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    private static String getMessage(Object obj) {
        if (obj == null) {
            return "";
        }

        if (obj instanceof Throwable) {
            StringWriter str = new StringWriter();
            PrintWriter pw = new PrintWriter(str);
            ((Throwable) obj).printStackTrace(pw);
            return str.toString();
        } else {
            return obj.toString();
        }
    }
}
