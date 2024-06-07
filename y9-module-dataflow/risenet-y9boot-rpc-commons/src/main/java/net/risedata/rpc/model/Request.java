package net.risedata.rpc.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.CharsetUtil;

import java.util.Arrays;

/**
 * 执行的参数
 */
public class Request {
    /**
     * rpc调用类型
     */
    public static final int RPC = 0;
    /**
     * 发送监听
     */
    public static final int LISTENER = 1;
    /**
     * 服务名
     */
    private String url;

    /**
     * 此消息的类型
     */
    private int type;

    /**
     * 参数
     */
    private JSONArray args;
    /**
     * 请求的id
     */
    private long id;


    public static Request as(Msg msg) {
        return new Request(msg.getMsg());

    }

    public Request() {
    }

    /**
     * 使用jsonstring的byte转换为对象
     *
     * @param jsonBytes
     */
    public Request(byte[] jsonBytes) {
        JSONObject jsonObject = JSONObject.parseObject(new String(jsonBytes, CharsetUtil.UTF_8));
        this.url = jsonObject.getString("url");
        this.args = jsonObject.getJSONArray("args");
        this.id = jsonObject.getLong("id");
        this.type = jsonObject.getInteger("type");
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public Request(String url, long id, Object... args) {
        this(url,RPC,id,args);
    }

    public Request(String url,int type, long id, Object... args) {
        this(url);
        this.type = type;
        if (args != null && args.length > 0) {
            JSONArray objects = new JSONArray();
            objects.addAll(Arrays.asList(args));
            this.args = objects;
        }
        this.id = id;
    }

    public Request(String url) {
        this.url = url;
    }

    public Msg asMsg() {
        byte[] bytes = this.toBytes();
        return new Msg(bytes.length, bytes);
    }

    public JSONArray getArgs() {
        return args;
    }

    public void setArgs(JSONArray args) {
        this.args = args;
    }

    public byte[] toBytes() {
        return JSON.toJSONString(this).getBytes(CharsetUtil.UTF_8);
    }


    public Long getId() {
        return id;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", type=" + type +
                ", args=" + args +
                ", id=" + id +
                '}';
    }
}
