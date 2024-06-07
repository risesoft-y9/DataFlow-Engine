package net.risedata.register.watch;

/**
 * @Description : 检查服务是否可用接口
 * @ClassName ServiceAvailability
 * @Author lb
 * @Date 2021/12/6 10:17
 * @Version 1.0
 */
public interface ServiceAvailability {
    /**
     * 是否可用
     * @return true=可用,false不可用
     */
    boolean isAvailability();
}
