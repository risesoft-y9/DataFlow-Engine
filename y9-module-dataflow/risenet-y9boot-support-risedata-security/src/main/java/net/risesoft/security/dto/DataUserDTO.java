package net.risesoft.security.dto;

import javax.validation.constraints.NotBlank;

import java.util.Date;

public class DataUserDTO {

	private String id;
	
	@NotBlank(message = "名字不能为空")
	private String userName;
	
	@NotBlank(message = "账号不能为空")
	private String account;
	
	@NotBlank(message = "密码不能为空")
	private String password;
	
	private Date createDate;

	public DataUserDTO() {
		super();
	}

	public DataUserDTO( String account, String password,String id) {
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
