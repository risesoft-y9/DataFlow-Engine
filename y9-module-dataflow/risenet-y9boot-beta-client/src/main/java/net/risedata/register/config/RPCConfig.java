package net.risedata.register.config;

import org.springframework.stereotype.Component;

import net.risedata.rpc.annotation.RPCScan;

/**
 * @Description :
 * @ClassName RPCConfig
 * @Author lb
 * @Date 2021/11/26 14:38
 * @Version 1.0
 */
@Component
@RPCScan("net.risedata.register.rpc")
public class RPCConfig {
}
