package net.risedata.register.system;

import cn.hutool.json.JSONUtil;

/**
 * @Description :
 * @ClassName test
 * @Author lb
 * @Date 2021/12/3 16:32
 * @Version 1.0
 */
public class test {
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.serverInfo();
        java.lang.System.out.println(JSONUtil.toJsonStr(server));
        long time = java.lang.System.currentTimeMillis();
        server.refreshCpu();
        server.refreshMem();
        server.refreshDisks();
        java.lang.System.out.println(java.lang.System.currentTimeMillis() - time);
        java.lang.System.out.println("cpu"+server.getCpu());
        java.lang.System.out.println();
        java.lang.System.out.println("内存"+server.getMem());
        java.lang.System.out.println();
        java.lang.System.out.println("桌面"+server.getDisks());
        java.lang.System.out.println(java.lang.System.currentTimeMillis()-time);

    }
}
