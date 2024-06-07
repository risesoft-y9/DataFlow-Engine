package net.risedata.rpc.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * @Description : key 锁
 * @ClassName KeyLock
 * @Author lb
 * @Date 2021/12/10 9:45
 * @Version 1.0
 */
public class KeyLock<K> {

    private Map<K, Semaphore> keys = new ConcurrentHashMap<>();

    private Semaphore createLock = new Semaphore(1);

    public void lock(K k) {
        Semaphore lock = keys.get(k);
        if (lock == null) {
            createLock.acquireUninterruptibly();
            lock = keys.get(k);
            if (lock==null){
                lock = new Semaphore(1);
                keys.put(k, lock);
            }
            createLock.release();
        }
        lock.acquireUninterruptibly();
    }


    public void unLock(K k) {
        Semaphore lock = keys.get(k);
        if (lock != null) {
            lock.release();
        }
    }

}
