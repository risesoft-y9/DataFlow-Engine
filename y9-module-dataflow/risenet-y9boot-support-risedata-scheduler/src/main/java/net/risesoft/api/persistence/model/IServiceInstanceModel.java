package net.risesoft.api.persistence.model;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.risedata.register.model.WatchProperties;
import net.risesoft.security.GetEnvironment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

/**
 * @Description : 实例数据 实体类
 * @ClassName ServerInstance
 * @Author lb
 * @Date 2021/11/25 17:28
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Table(name = "Y9_DATASERVICE_ISERVICE")
public class IServiceInstanceModel implements Serializable, GetEnvironment, GetService{

    public static Integer TRUE = 1;

    public static Integer FALSE = 0;
    /**
     * 实例id
     */
    @Id
    @Column(name = "INSTANCE_Id", length = 100)
    private String instanceId;
    /**
     * serviceId
     */
    @Column(name = "SERVICE_Id", length = 100)
    private String serviceId;

    /**
     * 实例地址
     */
    @Column(name = "HOST", length = 200)
    private String host;
    /**
     * 实例版本
     */
    @Column(name = "SERVICE_VERSION", length = 200)
    private String version;
    /**
     * 实例描述
     */
    @Column(name = "DESCRIPTION", length = 200)
    private String description;
    /**
     * 负责人信息
     */
    @Column(name = "MANAGER_INFO", length = 500)
    private String managerInfo;
    /**
     * 实例端口
     */
    @Column(name = "PORT", length = 10)
    private Integer port;
    /**
     * 是否https
     */
    @Column(name = "SECURE", length = 1)
    private Integer secure;
    /**
     * 日志文件产生路径 或者文件
     */
    @Column(name = "lOG_PATH", length = 500)
    private String logPath;
    /**
     * http OR https
     */

    @Column(name = "SCHEME", length = 100)
    private String scheme;
    /**
     * 携带信息 json
     */
    @Column(name = "METADATA", length = 500)
    private String metadata;
    /**
     * 监控信息
     */
    @Column(name = "WATCH_INFO", length = 1000)
    private String watchInfo;
    /***
     * 上下文
     */
    @Column(name = "CONTEXT", length = 100)
    private String context;
    /**
     * 过期时间 多少毫秒没注册则代表下线
     */
    @Column(name = "EXPIRES_TIME", length = 10)
    private Integer expiresTime;

    /**
     * 权重
     */
    @Column(name = "WEIGHT", length = 10)
    private Integer weight;
    /**
     * 状态
     */
    @Column(name = "STATUS", length = 1)
    private Integer status;
    /**
     * 是否为自定义的
     */
    @Column(name = "CUSTOM", length = 1)
    private Integer custom;
    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME", length = 40)
    private Long updateTime;
    /**
     * 注册时间
     */
    @Column(name = "REGISTER_TIME", length = 40)
    private Long registerTime;

    /**
     * 指定环境
     */
    @Column(name = "ENVIRONMENT", length = 100)
    private String environment;
    /**
     * 服务类型
     */
    @Column(name = "SERVICE_TYPE", length = 40)
    private String type;
    /**
     * 监控服务id
     */
    @Column(name = "WATCH_SERVER", length = 200)
    private String watchServer;


    public String getWatchServer() {
        return watchServer;
    }

    public void setWatchServer(String watchServer) {
        this.watchServer = watchServer;
    }

    public String getLogPath() {
        return logPath;
    }

    public Integer getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(Integer expiresTime) {
        this.expiresTime = expiresTime;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getStatus() {
        return status;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public Long getRegisterTime() {
        return registerTime;
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


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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


    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean getSecure() {
        return secure == TRUE;
    }

    public void setSecureForInt(Integer secure) {
        this.secure = secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure ? TRUE : FALSE;
    }

    public Map<String, Object> getMetadata() {
        return JSON.parseObject(this.metadata);
    }

    public void setMetadataForStr(String metadata) {
        this.metadata = metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = JSON.toJSONString(metadata);
    }

    @Transient
    private WatchProperties watchPropertiesCache;

    public WatchProperties getWatchInfo() {
        if (watchPropertiesCache == null) {
            watchPropertiesCache = JSON.parseObject(this.watchInfo, WatchProperties.class);
        }
        return watchPropertiesCache;
    }

    public void setWatchInfoForStr(String watchInfo) {
        this.watchInfo = watchInfo;
    }

    public void setWatchInfo(WatchProperties watchInfo) {
        this.watchInfo = JSON.toJSONString(watchInfo);
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean getCustom() {
        return custom == TRUE;
    }

    public void setCustom(boolean custom) {
        this.custom = custom ? TRUE : FALSE;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "IServiceInstanceModel{" +
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
                ", metadata='" + metadata + '\'' +
                ", watchInfo='" + watchInfo + '\'' +
                ", context='" + context + '\'' +
                ", status=" + status +
                ", custom=" + custom +
                ", updateTime=" + updateTime +
                ", registerTime=" + registerTime +
                ", environment='" + environment + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public String getService() {
        return serviceId;
    }
}
