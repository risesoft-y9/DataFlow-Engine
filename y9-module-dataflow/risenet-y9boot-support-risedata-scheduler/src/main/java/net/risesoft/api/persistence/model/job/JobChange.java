package net.risesoft.api.persistence.model.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

/**
 * @Description : 任务改变表存放着哪些任务改变了 对应的调度服务需要去更新 更新后删除记录
 * @ClassName JobChange
 * @Author lb
 * @Date 2022/9/14 11:47
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Table(name = "Y9_DATASERVICE_JOB_CHANGE")
public class JobChange {
	@Comment(value = "改变了的任务id")
    @Id
    @Column(name = "ID", length = 100)
    private Integer id;
}
