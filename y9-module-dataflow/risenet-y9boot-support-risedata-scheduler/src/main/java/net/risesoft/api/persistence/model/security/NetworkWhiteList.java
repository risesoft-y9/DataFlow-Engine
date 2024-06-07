package net.risesoft.api.persistence.model.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.risedata.jdbc.annotations.operation.Operate;
import net.risedata.jdbc.annotations.order.Desc;
import net.risedata.jdbc.operation.Operates;
import net.risesoft.api.persistence.config.LikeOperation;
import net.risesoft.api.persistence.model.GetEnvironment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Comment;

import java.util.Date;

/**
 * @Description : 网络请求白名单 ,包括配置文件访问权限  注册服务权限
 * @ClassName NetworkWhiteList
 * @Author lb
 * @Date 2022/8/5 9:54
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Table(name = "Y9_DATASERVICE_NETWORK_WHITE")
public class NetworkWhiteList implements GetEnvironment {

    /**
     * id
     */
    @Id
    @Comment(value = "id")
    @Column(name = "ID", length = 100)
    private String id;
    /**
     * 描述
     */
    @Comment(value = "描述")
    @NotBlank(message = "描述不能为空")
    @Operate(value = Operates.PLACEHOLDER, operation = LikeOperation.class)
    @Column(name = "DESCRIPTION", length = 100)
    private String description;
    /**
     * ip 匹配 多个使用, 分隔 可以使用** 匹配
     */
    @Comment(value = "ip匹配")
    @NotBlank(message = "ip不能为空")
    @Operate(value = Operates.PLACEHOLDER, operation = LikeOperation.class)
    @Column(name = "IP_MATCH", length = 100)
    private String ipMatch;

    /**
     * 可以注册的服务 多个使用, 分隔 可以使用** 匹配
     */
    @Comment(value = "服务匹配")
    @NotBlank(message = "服务不能为空")
    @Operate(value = Operates.PLACEHOLDER, operation = LikeOperation.class)
    @Column(name = "SERVICE", length = 100)
    private String service;

    /**
     * 所属环境 id
     */
    @Comment(value = "所属环境id")
    @NotBlank(message = "环境不能为空")
    @Operate(Operates.EQ)
    @Column(name = "ENVIRONMENT", length = 100)
    private String environment;
    /**
     * 创建时间
     */
    @Desc
    @Comment(value = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name = "CREATE_DATE", length = 100)
    private Date createDate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpMatch() {
        return ipMatch;
    }

    public void setIpMatch(String ipMatch) {
        this.ipMatch = ipMatch;
    }



    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }



}
