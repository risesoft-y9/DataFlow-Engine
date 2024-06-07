package net.risesoft.api.persistence.model.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import org.hibernate.annotations.Comment;

/**
 * @Description :  日志信息
 * @ClassName JobInfo
 * @Author lb
 * @Date 2022/9/14 11:47
 * @Version 1.0
 */
@Entity
@IdClass(JobInfoKey.class)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Table(name = "Y9_DATASERVICE_JOB_INFO")
public class JobInfo {
    /**
     * 时间
     */
	@Comment(value = "日志记录的时间")
    @Id
    @Column(name = "INFO_DATE", length = 100)
    private String date;
	@Comment(value = "成功的次数")
    @Column(name = "SUCCESS", length = 10)
    private Integer success;
	@Comment(value = "异常次数")
    @Column(name = "ERROR", length = 10)
    private Integer error;
    /**
     * 所属环境
     */
	@Comment(value = "所属环境")
    @Id
    @Column(name = "ENVIRONMENT", length = 10)
    private String environment;

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "JobInfo{" +
                "date='" + date + '\'' +
                ", success=" + success +
                ", error=" + error +
                '}';
    }
}
