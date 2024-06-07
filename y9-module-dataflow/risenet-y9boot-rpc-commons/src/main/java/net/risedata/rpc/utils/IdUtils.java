package net.risedata.rpc.utils;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class IdUtils {
    /**
     * 返回当前线程id 只适合同步操作
     *
     * @return
     */
    public static long getThreadId() {
        return Thread.currentThread().getId();
    }

    private static final long MAX = Long.MAX_VALUE - 1;

    private static long id = 0;

    public synchronized static long getId() {
        if (id > MAX) {
            id = 0;
        }
        return id++;
    }

    public static void refresh(){
        id = 0;
    }

}
