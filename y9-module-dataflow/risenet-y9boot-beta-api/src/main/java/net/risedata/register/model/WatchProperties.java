package net.risedata.register.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description : 服务监控配置信息
 * @ClassName WatchProperties
 * @Author lb
 * @Date 2021/12/3 15:41
 * @Version 1.0
 */
@ConfigurationProperties("beta.discovery.watch")
public class WatchProperties {


    /**
     * 是否监控
     */
    private boolean watch = false;
    /**
     * 当配置了这个参数后联系请求次数达到这个次数则自动上线
     * 如果是0则不会自动上线
     */
    private Integer success = 3;
    /**
     * 设置失败次数默认值为3
     * 当其他参数成立的时候也会被判断失败
     */
    private Integer fail = 3;
    /**
     * 间隔多少毫秒发送一次 默认 30秒
     */
    private Integer time = 30000;

    /**
     * 超时时间 超过这个时间则算失败 默认3秒
     */
    private Integer timeOut = 3000;
    /**
     * 上下文
     */
    private String baseContext = "";

    /**
     * 返回值正确的值(自定义生效)
     */
    private String successRet;

    private String watchUrl;

    public String getWatchUrl() {
        return watchUrl;
    }

    public void setWatchUrl(String watchUrl) {
        this.watchUrl = watchUrl;
    }

    public String getSuccessRet() {
        return successRet;
    }

    public void setSuccessRet(String successRet) {
        this.successRet = successRet;
    }

    public boolean isWatch() {
        return watch;
    }

    public void setWatch(boolean watch) {
        this.watch = watch;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getFail() {
        return fail;
    }

    public void setFail(Integer fail) {
        this.fail = fail;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }


    public Integer getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Integer timeOut) {
        this.timeOut = timeOut;
    }

    public String getBaseContext() {
        return baseContext;
    }

    public void setBaseContext(String baseContext) {
        this.baseContext = baseContext;
    }

    @Override
    public String toString() {
        return "WatchProperties{" +
                "watch=" + watch +
                ", success=" + success +
                ", fail=" + fail +
                ", time=" + time +
                ", timeOut=" + timeOut +
                ", baseContext='" + baseContext + '\'' +
                ", successRet='" + successRet + '\'' +
                ", watchUrl='" + watchUrl + '\'' +
                '}';
    }
}
