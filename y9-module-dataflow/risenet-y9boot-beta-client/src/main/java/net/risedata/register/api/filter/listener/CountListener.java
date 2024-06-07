package net.risedata.register.api.filter.listener;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description :
 * @ClassName CountListener
 * @Author lb
 * @Date 2022/7/4 17:11
 * @Version 1.0
 */
public class CountListener implements HttpListener {
    private int count = 0;
    private List<Runnable> runables = new ArrayList<>();

    public synchronized void add(Runnable run) {
        if (count == 0) {
            run.run();
        } else {
            runables.add(run);
        }
    }

    @Override
    public synchronized void onStart(ServletRequest request, ServletResponse response) {
        count++;
    }

    @Override
    public synchronized void onEnd(ServletRequest request, ServletResponse response) {
        count--;
        if (count == 0) {
            for (Runnable run : runables) {
                run.run();
            }
            runables.clear();
        }
    }

    @Override
    public void onError(ServletRequest request, ServletResponse response, Exception e) {

    }

    public int getCount() {

        return count;
    }
}
