package net.risesoft.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * @Description : 环境
 * @ClassName Environment
 * @Author lb
 * @Date 2022/8/4 16:54
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
@Table(name = "Y9_DATASERVICE_ENVIRONMENT")
public class Environment implements Serializable {

	private static final long serialVersionUID = 610072904557720522L;

	public static final String PUBLIC = "Public";

	@Id
	@Column(name = "ID", length = 36)
	private String id;

	/**
	 * 环境名字
	 */

	@Comment(value = "环境名字")
	@NotBlank(message = "环境名字不能为空")
	@Column(name = "NAME", length = 100)
	private String name;

	/**
	 * 环境描述
	 */
	@Comment(value = "环境描述")
	@NotBlank(message = "环境描述不能为空")
	@Column(name = "DESCRIPTION", length = 200)
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
