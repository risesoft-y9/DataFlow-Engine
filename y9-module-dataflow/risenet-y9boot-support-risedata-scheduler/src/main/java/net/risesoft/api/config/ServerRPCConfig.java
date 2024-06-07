package net.risesoft.api.config;

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
@RPCScan("net.risesoft.api.consumer")
public class ServerRPCConfig {
}
