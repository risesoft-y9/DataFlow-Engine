package net.risesoft.api.persistence.model.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.risedata.jdbc.annotations.order.Desc;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description : 操作日志
 * @ClassName Log
 * @Author lb
 * @Date 2022/8/5 10:03
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Table(name = "Y9_DATASERVICE_LOG")
public class Log implements Serializable {
    /**
     * id
     */
    @Id
    @Column(name = "ID", length = 100)
    private String id;
    /**
     * 日志产生服务id
     */
    @Column(name = "INS_ID", length = 200)
    private String insId;

    /**
     * ip
     */
    @Column(name = "IP", length = 100)
    private String ip;

    /**
     * 环境
     */
    @Column(name = "ENVIRONMENT", length = 100)
    private String environment;


    /**
     * 创建时间
     */
    @Desc
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    @Column(name = "CREATE_DATE", length = 100)
    private Date createDate;
    /**
     * 内容
     */
    @Column(name = "CONTEXT", length = 500)
    private String context;

    /**
     * 类型
     */
    @Column(name = "TYPE", length = 100)
    private String type;

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInsId() {
        return insId;
    }

    public void setInsId(String insId) {
        this.insId = insId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id='" + id + '\'' +
                ", insId='" + insId + '\'' +
                ", ip='" + ip + '\'' +
                ", createDate=" + createDate +
                ", context='" + context + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
