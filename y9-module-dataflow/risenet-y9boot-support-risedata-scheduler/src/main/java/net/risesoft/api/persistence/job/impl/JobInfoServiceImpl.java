package net.risesoft.api.persistence.job.impl;

import cn.hutool.core.date.DateUtil;
import net.risesoft.api.persistence.dao.job.JobInfoDao;
import net.risesoft.api.persistence.job.JobInfoService;
import net.risesoft.api.persistence.model.job.JobInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description :
 * @ClassName JobInfoServiceImpl
 * @Author lb
 * @Date 2022/9/20 17:00
 * @Version 1.0
 */
@Service
public class JobInfoServiceImpl implements JobInfoService {

    @Autowired
    JobInfoDao dao;

    private String date;

    @Override
    public Long getCount(String environment) {
        return dao.getCount(environment);
    }

    @Override
    public List<JobInfo> getInfo(String environment, String startTime, String endTime) {

        return dao.search(environment, startTime, endTime);
    }

    private String getDate() {
        if (date == null) {
            this.refresh();
        }
        return date;
    }

    /**
     * 每天0 点开始执行刷新时间
     */
    @Scheduled(cron = "0 0,1 0 1/1 * ?")
    private void refresh() {
        date = DateUtil.format(new Date(), "yyyy-MM-dd");
    }

    @Override
    public void addSuccess(String environment) {
        if (dao.addSuccess(getDate(), environment) < 1) {
            create(environment);
            dao.addSuccess(getDate(), environment);
        }
    }


    private void create(String environment) {
        dao.create(getDate(), environment);
    }

    @Override
    public void addError(String environment) {
        if (dao.addError(getDate(), environment) < 1) {
            create(environment);
            dao.addError(getDate(), environment);
        }
    }


}
