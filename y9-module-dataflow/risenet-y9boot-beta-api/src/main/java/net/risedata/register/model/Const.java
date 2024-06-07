package net.risedata.register.model;

/**
 * @Description :
 * @ClassName Const
 * @Author lb
 * @Date 2021/11/25 17:19
 * @Version 1.0
 */
public class Const {
    public static final String REMOVE_LISTENER="CLOUD_REGISTER_REMOVE_LISTENER";
    public static final String CHANGE_LISTENER="CLOUD_REGISTER_CHANGE_LISTENER";
    /**
     * 注册信息修改
     */
    public static final String REGISTER_LISTENER="CLOUD_REGISTER_REGISTER_LISTENER";
    /**
     * 被移除的服务修改
     */
    public static final String REGISTER_REMOVE_CHANGE_LISTENER="CLOUD_REGISTER_REMOVE_CHANGE_LISTENER";

    /**
     * meta 信息修改
     */
    //public static final String REGISTER_LISTENER_META="REGISTER_LISTENER_META";


    public static final String WATCH_URL="REGISTER/WATCH";
    public static final String CONFIG_CHANGE="CONFIG_CHANGE";
    public static final String CHECK="/CHECK";
    /**
     * 重启事件
     */
    public static final String SYS_RESTART="REGISTER_SYS_RESTART";
    /**
     * 停止
     */
    public static final String SYS_STOP="REGISTER_SYS_STOP";
}
