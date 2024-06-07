package net.risedata.rpc.model;

/**
 * @Description : 监听器返回
 * @ClassName ListenerRequest
 * @Author lb
 * @Date 2022/6/30 10:59
 * @Version 1.0
 */
public class ListenerResponse {

    /**
     * 状态
     */
    private Integer status;
    /**
     * 这个msg 为json string
     */
    private String msg;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ListenerResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }
}
