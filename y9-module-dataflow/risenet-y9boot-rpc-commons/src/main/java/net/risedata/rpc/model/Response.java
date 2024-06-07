package net.risedata.rpc.model;


import com.alibaba.fastjson.JSONObject;
import io.netty.util.CharsetUtil;

/**
 * 返回信息
 */
public class Response {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    /**
     * 此消息的类型
     */
    private int type;
    /**
     * 状态
     */
    private int status;
    /**
     * 返回值
     */
    private String message;

    private long id;


    public static Response as(Msg msg){
        return new Response(msg.getMsg());
    }

    /**
     * json 的byte 转换为对象
     *
     * @param jsonBytes
     */
    public Response(byte[] jsonBytes) {
        JSONObject jsonObject = JSONObject.parseObject(new String(jsonBytes, CharsetUtil.UTF_8));
        this.message = jsonObject.getString("message");
        this.status = jsonObject.getInteger("status");
        this.type = jsonObject.getInteger("type");
        this.message = jsonObject.getString("message");
        this.id = jsonObject.getLong("id");
    }

    public byte[] toBytes() {
        return JSONObject.toJSONString(this).getBytes(CharsetUtil.UTF_8);
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Response(int status, String message, long id) {
        this.status = status;
        this.message = message;
        this.id = id;
    }
    public Response() {

    }
    public static int getSUCCESS() {
        return SUCCESS;
    }

    public static int getERROR() {
        return ERROR;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    

    public Msg asMsg() {
        byte[] bytes = this.toBytes();
        return new Msg(bytes.length, bytes);
    }

    @Override
    public String toString() {
        return "Response{" +
                "type=" + type +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", id=" + id +
                '}';
    }

    public long getId() {
        return this.id;
    }
}
