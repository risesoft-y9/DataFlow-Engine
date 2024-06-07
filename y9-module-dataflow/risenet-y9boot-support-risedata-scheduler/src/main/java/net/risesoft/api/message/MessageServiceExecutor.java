package net.risesoft.api.message;

/**
 * @Description : 发送消息 服务
 * @ClassName MessageService
 * @Author lb
 * @Date 2022/8/30 10:22
 * @Version 1.0
 */
public interface MessageServiceExecutor {

    /**
     * 发送消息服务
     * @param recipients 收消息人
     * @param title 标题
     * @param message 短信
     */
    void sendMessage(String[] recipients,String title,String message,Object source);


}
