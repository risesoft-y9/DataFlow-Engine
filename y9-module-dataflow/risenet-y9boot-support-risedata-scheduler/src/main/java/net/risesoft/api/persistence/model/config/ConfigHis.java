package net.risesoft.api.persistence.model.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.risedata.jdbc.annotations.operation.Operate;
import net.risedata.jdbc.annotations.order.Desc;
import net.risedata.jdbc.operation.Operates;
import net.risesoft.security.GetEnvironment;
import net.risesoft.security.LikeOperation;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description : 配置文件历史版本
 * @ClassName ConfigModel
 * @Author lb
 * @Date 2022/8/2 10:39
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Table(name = "Y9_DATASERVICE_CONFIG_HIS")
public class ConfigHis implements Serializable, GetEnvironment {
    @Id
    @Column(name = "ID", length = 100)
    private String id;
    /**
     * 配置文件id
     */

    @Column(name = "CONFIG_ID", length = 100)
    private String configId;

    /**
     * 名字
     */
    @Operate(value = Operates.PLACEHOLDER, operation = LikeOperation.class)
    @Column(name = "NAME", length = 100)
    private String name;
    /**
     * 类型
     */
    @Column(name = "CONFIG_TYPE", length = 100)
    private String type;
    /**
     * 分组
     */
    @Operate(value = Operates.PLACEHOLDER, operation = LikeOperation.class)
    @Column(name = "GROUP_NAME", length = 100)
    private String group;
    /**
     * 所属环境 id
     */
    @Operate(Operates.EQ)
    @Column(name = "ENVIRONMENT", length = 100)
    private String environment;
    /**
     * 最后的修改时间
     */
    @Desc
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone ="GMT+8")
    @Column(name = "UPDATETIME", length = 100)
    private Date updateTime;
    /**
     * 配置文件内容
     */
    @Lob
    @Column(name = "CONTENT", length = 4000)
    private String content;
    /**
     * 操作人名字
     */
    @Column(name = "OP_NAME", length = 100)
    private String opName;
    /**
     * 操作
     */
    @Column(name = "OP_TYPE", length = 100)
    private String opType;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    @Override
    public String toString() {
        return "ConfigHis{" +
                "id='" + id + '\'' +
                ", configId='" + configId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", group='" + group + '\'' +
                ", environment='" + environment + '\'' +
                ", updateTime=" + updateTime +
                ", opName='" + opName + '\'' +
                ", opType='" + opType + '\'' +
                '}';
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

}
