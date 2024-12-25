package net.risesoft.api.persistence.model.log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * @Description : 匹配分析
 * @ClassName MatchAnalyseModel
 * @Author lb
 * @Date 2023/11/23 10:01
 * @Version 1.0
 */
@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Table(name = "Y9_DATASERVICE_LOG_M_ANALYSE")
public class MatchAnalyseModel implements Serializable {

    /**
     * id
     */
    @Id
    @Comment(value = "id")
    @Column(name = "ID", length = 100)
    private String id;
    /**
     * 匹配中的前提
     */
    @Comment(value = "匹配中的前提")
    @Column(name = "LOG_MATCH", length = 100)
    private String match;
    /**
     * 匹配时候的截取日志正则 多个使用~~来截取截止 start~~end 2个位一对 一个为获取关键信息 如果为数字则是在前一个位置加多少字数
     */
    @Comment(value = "截取日志")
    @Column(name = "SUB_MATCH", length = 100)
    private String subMatch;

    /**
     * 解决方案
     */
    @Comment(value = "解决办法")
    @Column(name = "SOLUTION", length = 400)
    private String solution;
    /**
     * 类型
     */
    @Comment(value = "类型")
    @Column(name = "MATCH_TYPE", length = 40)
    private String type;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getSubMatch() {
        return subMatch;
    }

    public void setSubMatch(String subMatch) {
        this.subMatch = subMatch;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
