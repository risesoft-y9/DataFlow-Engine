package net.risedata.rpc.provide.config;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4J2LoggerFactory;

public class Application {

    public static final InternalLogger logger = Log4J2LoggerFactory.getInstance(Application.class);

    public static final String GET_PORT_URL = "/RPC/PROVIDE/PORT";
}
