package net.risesoft.api.config.model;

import org.springframework.beans.factory.annotation.Value;

/**
 * @Description : 保存着定义信息
 * @ClassName ServiceConfigModel
 * @Author lb
 * @Date 2022/8/25 16:58
 * @Version 1.0
 */
public class ServiceConfigModel {

    @Value("${beta.discovery.service:${spring.application.name:}}")
    String myService;


    public String getMyService() {
        return myService;
    }

    public void setMyService(String myService) {
        this.myService = myService;
    }
}
