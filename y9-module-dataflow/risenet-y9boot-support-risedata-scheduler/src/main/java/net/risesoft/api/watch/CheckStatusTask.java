package net.risesoft.api.watch;

import cn.hutool.http.HttpUtil;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.risedata.register.model.Const;
import net.risedata.register.model.WatchProperties;
import net.risedata.register.service.IServiceInstance;
import net.risesoft.api.persistence.iservice.IServiceService;
import net.risesoft.api.persistence.model.IServiceInstanceModel;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description : 检查连接信息的任务
 * @ClassName CheckStatusTask
 * @Author lb
 * @Date 2021/12/6 17:17
 * @Version 1.0
 */
public class CheckStatusTask implements TimerTask {
    private String instanceId;

    /**
     * 连续报错次数
     */
    private int errorCout = 0;
    /**
     * 连续成功次数
     */
    private int successCount = 0;
    
    private String myInstanceId;
    
    private IServiceService iServiceService;

    public CheckStatusTask(String instanceId, String myInstanceId, IServiceService iServiceService) {
        this.instanceId = instanceId;
        this.myInstanceId = myInstanceId;
        this.iServiceService = iServiceService;
    }


    public static boolean check(IServiceInstanceModel iServiceInstance) {
        String checkUrl = createCheckUrl(iServiceInstance);

        String retStr = HttpUtil.get(checkUrl, iServiceInstance.getWatchInfo().getTimeOut());

        if (WatchManager.LOGGER.isDebugEnabled()) {
            WatchManager.LOGGER.debug(iServiceInstance.getInstanceId() + "check instance for url" + retStr);
        }
        Boolean res;
        if (!StringUtils.isEmpty(iServiceInstance.getWatchInfo().getWatchUrl())) {
            if (!StringUtils.isEmpty(iServiceInstance.getWatchInfo().getSuccessRet())) {
                res = iServiceInstance.getWatchInfo().getSuccessRet().equals(retStr);
            } else {
                res = true;
            }
        } else {
            res = Boolean.valueOf(retStr);
        }
        return res;
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        if (!WatchManager.hasTask(this, instanceId)) {
            return;
        }
        IServiceInstanceModel iServiceInstance = iServiceService.findWatchById(instanceId);
        if (iServiceInstance == null || iServiceInstance.getWatchInfo().isWatch() == false) {
            WatchManager.removeTask(instanceId, this);
            return;
        }
        try {

            Boolean res = check(iServiceInstance);
            if (res) {
                successCount++;
                errorCout = 0;
            } else {
                errorCout++;
                successCount = 0;
            }
        } catch (Exception e) {
            errorCout++;
            successCount = 0;
        }


        if (iServiceInstance.getStatus() == IServiceInstance.SUCCESS) {
            if (errorCout >= iServiceInstance.getWatchInfo().getFail()) {

                iServiceService.setStatus(iServiceInstance.getInstanceId(), IServiceInstance.ERROR);
            }
        } else if (iServiceInstance.getStatus() == IServiceInstance.ERROR) {

            if (iServiceInstance.getWatchInfo().getSuccess() > 0 && successCount >= iServiceInstance.getWatchInfo().getSuccess()) {

                iServiceService.setStatus(iServiceInstance.getInstanceId(), IServiceInstance.SUCCESS);
            }
        }
        if (iServiceInstance.getStatus() != IServiceInstance.AWAIT) {
            timeout.timer().newTimeout(this, iServiceInstance.getWatchInfo().getTime() * (errorCout > 3 ? errorCout > 10 ? 10 : errorCout : 1), TimeUnit.MILLISECONDS);
        }

    }

    public String getInstanceId() {
        return instanceId;
    }

    public int getErrorCout() {
        return errorCout;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public static String createCheckUrl(IServiceInstanceModel watchInfo) {
        WatchProperties info = watchInfo.getWatchInfo();
        StringBuilder urlBuild = new StringBuilder();
        urlBuild.append((watchInfo.getSecure() ? "https://" : "http://"));
        urlBuild.append(watchInfo.getHost()).append(":")
                .append(watchInfo.getPort()).append("/")
                .append(watchInfo.getWatchInfo().getBaseContext()).append(StringUtils.isEmpty(info.getWatchUrl()) ? Const.WATCH_URL : watchInfo.getWatchInfo().getWatchUrl())
                .append(Const.CHECK);
        return urlBuild.toString();
    }

    @Override
    public String toString() {
        return "CheckStatusTask{" +
                "instanceId='" + instanceId + '\'' +
                ", errorCout=" + errorCout +
                ", successCount=" + successCount +
                '}';
    }
}
