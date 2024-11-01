package net.risesoft.security.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataUser implements Serializable {

	private static final long serialVersionUID = 6504983651935464942L;

	private String id;
	
	/**
	 * 用户名
	 */
	private String userName;
	
	/**
	 * 账号
	 */
	private String account;
	
}
