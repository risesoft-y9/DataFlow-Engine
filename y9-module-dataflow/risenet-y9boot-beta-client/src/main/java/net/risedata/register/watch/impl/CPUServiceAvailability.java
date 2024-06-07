package net.risedata.register.watch.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import net.risedata.register.system.Cpu;

import java.util.logging.Logger;

/**
 * @Description : 根据cpu的状态判断是否可用
 * @ClassName CPUServiceAvailability
 * @Author lb
 * @Date 2021/12/6 10:18
 * @Version 1.0
 */
@ConfigurationProperties("register.discovery.watch")
public class CPUServiceAvailability extends  SystemAvailability {

    /**
     * 当cpu利用率高达多少的时候且拥有其他可用服务时停止此服务
     * 当此参数为0.0的时候不会监控
     */
    @Value("${register.discovery.watch.cpuError:0.0}")
    private Double cpuError = 0.0;

    public CPUServiceAvailability(){
        LOGGER.info("create cpu");
    }

    @Override
    public boolean isAvailability() {
        if (cpuError==0.0){
            return true;
        }
        Cpu cpu = getConcurrentCpu();
        if (cpu.getUsed()>cpuError){
            LOGGER.info("cpu error "+cpu.getUsed());
            return false;
        }
        return true;
    }
}
