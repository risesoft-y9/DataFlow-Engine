package net.risesoft.api.persistence.model.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.risedata.jdbc.annotations.operation.Operate;
import net.risedata.jdbc.operation.Operates;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description :
 * @ClassName User
 * @Author lb
 * @Date 2022/8/3 10:12
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@Table(name = "Y9_DATASERVICE_USER")
public class DataUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Comment(value = "id")
	@Column(name = "ID", length = 100)
	private String id;
	/**
	 * 用户名
	 */
	@Comment(value = "用户名")
	@NotBlank(message = "名字不能为空")
	@Operate(Operates.EQ)
	@Column(name = "USER_NAME", length = 100)
	private String userName;
	/**
	 * 账号
	 */
	@Comment(value = "账号")
	@NotBlank(message = "账号不能为空")
	@Operate(Operates.EQ)
	@Column(name = "ACCOUNT", length = 100)
	private String account;
	/**
	 * 密码
	 */
	@Comment(value = "密码")
	@Operate(Operates.EQ)
	@Column(name = "PASSWORD", length = 100)
	private String password;
	/**
	 * 创建时间
	 */
	@Comment(value = "创建时间")
	@Column(name = "CREATE_DATE", length = 100)
	private Date createDate;

	public DataUser() {
		super();
	}

	public DataUser( String account, String password,String id) {
		super();
		this.account = account;
		this.password = password;
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Override
	public String toString() {
		return "User{" + ", userName='" + userName + '\'' + ", password='" + password + '\'' + '}';
	}
}
