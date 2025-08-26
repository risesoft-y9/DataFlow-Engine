package net.risesoft.y9public.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.risesoft.base.BaseEntity;

@Entity
@Table(name = "Y9_DATASERVICE_DATACACHE")
@org.hibernate.annotations.Table(comment = "任务中的缓存数据", appliesTo = "Y9_DATASERVICE_DATACACHE")
@NoArgsConstructor
@Data
public class DataCacheEntity extends BaseEntity {

	private static final long serialVersionUID = -4869702601309597124L;

	@Id
	@Column(name = "CACHE_KEY", length = 200, nullable = false)
	@Comment(value = "key值")
	private String cacheKey;

	@Lob
	@Column(name = "CACHE_VALUE")
	@Comment(value = "value值")
	private String cacheValue;

}