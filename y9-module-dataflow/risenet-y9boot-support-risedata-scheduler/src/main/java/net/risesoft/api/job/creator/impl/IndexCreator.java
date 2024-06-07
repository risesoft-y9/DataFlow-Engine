package net.risesoft.api.job.creator.impl;


import net.risedata.rpc.utils.StringUtils;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.creator.CreatorMethod;

import org.springframework.stereotype.Component;

/**
 * @Description : index 操作
 * @ClassName IndexCreator
 * @Author lb
 * @Date 2022/10/17 9:20
 * @Version 1.0
 */
@Component("index")
public class IndexCreator implements CreatorMethod {


    @Override
    public String create(JobContext context, String[] args) {
        return  context.getRequiredValue("index")+"";
    }
}
