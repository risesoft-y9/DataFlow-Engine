package net.risesoft.api.message;


/**
 * @Description : 消息创建器
 * @ClassName MessageCreator
 * @Author lb
 * @Date 2022/8/30 15:04
 * @Version 1.0
 */
public interface MessageCreator<T> {

    String createContent(T t);

    String createTitle(T t);
}
