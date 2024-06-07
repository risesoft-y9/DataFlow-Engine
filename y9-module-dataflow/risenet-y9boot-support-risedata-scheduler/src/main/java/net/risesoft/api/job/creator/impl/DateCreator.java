package net.risesoft.api.job.creator.impl;


import net.risedata.jdbc.commons.utils.DateUtils;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.creator.CreatorMethod;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description : 时间操作 参数args 0 为时间格式默认yyyy-MM-dd HH:mm:SS
 * @ClassName DateCreator
 * @Author lb
 * @Date 2022/10/17 9:20
 * @Version 1.0
 */
@Component("date")
public class DateCreator implements CreatorMethod {


    @Override
    public String create(JobContext context, String[] args) {
        return DateUtils.format(new Date(),args.length>0?args[0]:"yyyy-MM-dd HH:mm:SS");
    }


}
