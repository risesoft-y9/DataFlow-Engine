package net.risesoft.api.persistence.job.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.api.persistence.dao.job.JobChangeDao;
import net.risesoft.api.persistence.job.JobChangeService;
import net.risesoft.api.persistence.model.job.Job;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Description :
 * @ClassName JobChangeServiceImpl
 * @Author lb
 * @Date 2022/9/14 11:49
 * @Version 1.0
 */
@Service
public class JobChangeServiceImpl implements JobChangeService {

    @Autowired
    JobChangeDao jobChangeDao;

    @Override
    public void insertChange(Integer jobId) {
        if (jobChangeDao.findById(jobId) == 0) {
            jobChangeDao.insert(jobId);
        }
    }

    @Override
    public List<Integer> searchChangeJobs() {

        return jobChangeDao.searchChangeJobs();
    }

    @Override
    public void delete(Integer jobId) {
        jobChangeDao.delete(jobId);
    }

    @Override
    public void onDelete(Collection<Integer> ids) {
        jobChangeDao.batchDelete(ids);
    }

}
