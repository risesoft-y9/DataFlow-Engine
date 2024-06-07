package net.risedata.register.service;

import java.util.Map;

import net.risedata.register.model.WatchProperties;

/**
 * @Description : 实例的数据
 * @ClassName ServerInstance
 * @Author lb
 * @Date 2021/11/25 17:28
 * @Version 1.0
 */
public class IServiceInstance {
    /**
     * 设置连接状态为等待
     */
    public static final int AWAIT = 1;
    /**
     * 成功状态
     */
    public static final int SUCCESS = 0;
    /**
     * 异常状态
     */
    public static final int ERROR = 2;
    /**
     * 是否暂时停用下次注册的时候自动恢复状态
     */
    public static final int AWAIT_STOP = 3;
    /**
     * 状态 应用服务器
     */
    public static final String TYPE_APPLICATION = "application";
    /**
     * 状态 nginx 代理
     */
    public static final String TYPE_NGINX = "nginx";
    /**
     * 类型其他
     */
    public static final String TYPE_OTHER = "other";
    /**
     * 类型tomcat
     */
    public static final String TYPE_TOMCAT = "tomcat";

    /**
     * 类型essearch
     */
    public static final String TYPE_ES = "es";

    /**
     * 实例id
     */
    private String instanceId;
    /**
     * serviceId
     */
    private String serviceId;
    /**
     * 实例地址
     */
    private String host;
    /**
     * 实例版本
     */
    private String version;
    /**
     * 实例描述
     */
    private String description;
    /**
     * 负责人信息
     */
    private String managerInfo;
    /**
     * 实例端口
     */
    private int port;
    /**
     * 是否https
     */
    private boolean secure;
    /**
     * 日志文件产生路径 或者文件
     */
    private String logPath;
    /**
     * http OR https
     */
    private String scheme;
    /**
     * 携带信息
     */
    private Map<String, String> metadata;
    /**
     * 监控信息
     */
    private WatchProperties watchInfo;
    /***
     * 上下文
     */
    private String context;

    /**
     * 状态
     */
    private int status = 0;
    /**
     * 是否为自定义的
     */
    private boolean custom;
    /**
     * 最近更新时间
     */
    private long updateTime;
    /**
     * 注册时间
     */
    private long registerTime;
    /**
     * 指定环境
     */
    private String environment;
    /**
     * 过期时间 多少毫秒没注册则代表下线
     */
    private int expiresTime;
    /**
     * 权重
     */
    private int weight;

    /**
     * 服务类型
     */
    private String type;

    public String getLogPath() {
        return logPath;
    }

    public int getExpiresTime() {
        return expiresTime;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setExpiresTime(int expiresTime) {
        this.expiresTime = expiresTime;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public WatchProperties getWatchInfo() {
        return watchInfo;
    }

    public void setWatchInfo(WatchProperties watchInfo) {
        this.watchInfo = watchInfo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean getCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManagerInfo() {
        return managerInfo;
    }

    public void setManagerInfo(String managerInfo) {
        this.managerInfo = managerInfo;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean getSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "IServiceInstance{" +
                "instanceId='" + instanceId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", host='" + host + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", managerInfo='" + managerInfo + '\'' +
                ", port=" + port +
                ", secure=" + secure +
                ", logPath='" + logPath + '\'' +
                ", scheme='" + scheme + '\'' +
                ", metadata=" + metadata +
                ", watchInfo=" + watchInfo +
                ", context='" + context + '\'' +
                ", status=" + status +
                ", custom=" + custom +
                ", updateTime=" + updateTime +
                ", registerTime=" + registerTime +
                ", environment='" + environment + '\'' +
                ", expiresTime=" + expiresTime +
                ", type='" + type + '\'' +
                '}';
    }
}
