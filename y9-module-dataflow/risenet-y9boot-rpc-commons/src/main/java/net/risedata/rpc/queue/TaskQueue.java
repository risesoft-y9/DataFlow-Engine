package net.risedata.rpc.queue;

import java.util.LinkedList;
import java.util.Queue;

import net.risedata.rpc.Task.Task;

/**
 * 任务队列 单线程的任务队列 多线程请使用java 自带的 任务线程池执行
 *
 * @author libo
 * 2020年8月11日
 */
public class TaskQueue implements Runnable {
    private Queue<Task> q = new LinkedList<Task>();
    private Object o = new Object();
    private boolean isNotify = false;

    public void add(Task t) {
        synchronized (q) {
            q.add(t);
        }
        if (!isNotify) {
            synchronized (o) {
                if (!isNotify) {
                    isNotify = true;
                    o.notify();
                }
            }
        }
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        Task t;
        while (true) {

            while (!q.isEmpty()) {
                synchronized (q) {
                    t = q.poll();
                }
                if (t != null) {
                    try {
                        t.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
        }

        try {
            synchronized (o) {
                if (isNotify) {
                    isNotify = false;
                }
                o.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
}
