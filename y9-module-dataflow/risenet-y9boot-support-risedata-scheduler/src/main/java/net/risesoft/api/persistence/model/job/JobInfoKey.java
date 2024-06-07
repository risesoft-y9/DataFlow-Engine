package net.risesoft.api.persistence.model.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Description :  日志信息
 * @ClassName JobInfo
 * @Author lb
 * @Date 2022/9/14 11:47
 * @Version 1.0
 */

public class JobInfoKey implements Serializable {
    /**
     * 时间
     */
    @Id
    @Comment(value = "时间")
    @Column(name = "INFO_DATE", length = 100)
    private String date;


    /**
     * 所属环境
     */
    @Id
    @Comment(value = "任务所属环境")
    @Column(name = "ENVIRONMENT", length = 50)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobInfoKey that = (JobInfoKey) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(environment, that.environment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, environment);
    }
}
