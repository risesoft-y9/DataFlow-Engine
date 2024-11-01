package net.risesoft.api.persistence.model.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.risedata.jdbc.annotations.operation.Operate;
import net.risedata.jdbc.annotations.order.Desc;
import net.risedata.jdbc.operation.Operates;
import net.risesoft.security.GetEnvironment;
import net.risesoft.security.LikeOperation;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description : 配置文件
 * @ClassName ConfigModel
 * @Author lb
 * @Date 2022/8/2 10:39
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Table(name = "Y9_DATASERVICE_CONFIG")
public class Config implements Serializable, GetEnvironment {
    /**
     * 日志类型
     */
    public static final String LOG_TYPE = "CONFIG_LOG";
    /**
     * 配置文件默认分组
     */
    public static final String DEFAULT_GROUP = "DEFAULT_GROUP";
    /**
     * 配置文件类型 yaml
     */
    public static final String YAML_TYPE = "yaml";
    /**
     * 配置文件类型 json
     */
    public static final String JSON_TYPE = "json";
    /**
     * 配置文件类型 properties
     */
    public static final String PROPERTIES_TYPE = "properties";
    /**
     * 配置文件id
     */
    @Id
    @Column(name = "ID", length = 100)
    private String id;

    /**
     * 名字
     */
    @Desc
    @NotBlank(message = "名字不能为空")
    @Operate(value = Operates.PLACEHOLDER, operation = LikeOperation.class)
    @Column(name = "NAME", length = 100)
    private String name;

    /**
     * 描述
     */
    @Operate(value = Operates.PLACEHOLDER, operation = LikeOperation.class)
    @Column(name = "DESCRIPTION", length = 200)
    private String description;
    /**
     * 类型
     */
    @NotBlank(message = "类型不能为空")
    @Column(name = "CONFIG_TYPE", length = 100)
    private String type;
    /**
     * 分组
     */
    @NotBlank(message = "分组不能为空")
    @Operate(value = Operates.PLACEHOLDER, operation = LikeOperation.class)
    @Column(name = "GROUP_NAME", length = 100)
    private String group;
    /**
     * 所属环境 id
     */
    @Operate(Operates.EQ)
    @NotBlank(message = "环境不能为空")
    @Column(name = "ENVIRONMENT", length = 100)
    private String environment;
    /**
     * 配置文件内容
     */
    @Lob
    @NotBlank(message = "文件内容不能为空")
    @Column(name = "CONTENT", length = 8000)
    private String content;
    /**
     * 创建时间
     */
    @Column(name = "CREATE_DATE")
    private Date createDate;
    /**
     * 最新修改时间
     */
    @Column(name = "UPDATE_DATE")
    private Date updateDate;
    public Date getCreateDate() {
        return createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Config{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", group='" + group + '\'' +
                ", environment='" + environment + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
