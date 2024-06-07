package net.risesoft.api.persistence.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * @Description : 令牌 保存令牌对应的用户 以及令牌产生时间
 * @ClassName Token
 * @Author lb
 * @Date 2022/8/3 15:16
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@Table(name = "Y9_DATASERVICE_TOKEN")
public class Token implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 令牌
     */
    @Id
    @Comment(value = "令牌id")
    @Column(name = "TOKEN",length = 38)
    private String token;

    /**
     * 所对应的userId
     */
    @Comment(value = "用户id")
    @Column(name = "USER_ID",length = 38)
    private String userId;

    /**
     * 令牌时间
     */
    @Comment(value = "令牌时间")
    @Column(name = "TOKEN_TIME",length = 38)
    private Long tokenTime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTokenTime() {
        return tokenTime;
    }

    public void setTokenTime(Long tokenTime) {
        this.tokenTime = tokenTime;
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", userId='" + userId + '\'' +
                ", tokenTime=" + tokenTime +
                '}';
    }
}
