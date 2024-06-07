package net.risedata.register.watch.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import net.risedata.register.system.Cpu;
import net.risedata.register.system.Mem;

/**
 * @Description : 根据内存的状态判断是否可用
 * @ClassName CPUServiceAvailability
 * @Author lb
 * @Date 2021/12/6 10:18
 * @Version 1.0
 */
@ConfigurationProperties("register.discovery.watch")
public class MEMServiceAvailability extends SystemAvailability {

    /**
     * 内存危险值
     */
    @Value("${register.discovery.watch.memError:0.0}")
    private Double memError = 0.0;

    public MEMServiceAvailability(){

     LOGGER.info("create mem");
    }

    @Override
    public boolean isAvailability() {
        if (memError == 0.0) {
            return true;
        }
        Mem mem = getConcurrentMem();
        if (mem.getUsedScale() > memError) {
            LOGGER.error("mem error "+mem.getUsedScale());
            return false;
        }
        return true;
    }
}
