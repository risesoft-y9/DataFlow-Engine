package net.risesoft.api.job.creator.impl;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;

import net.risedata.jdbc.commons.utils.DateUtils;
import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.creator.CreatorMethod;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * @Description :http 请求 args[0]  采取的方法为post 请求args[1] 为 参数
 * @ClassName HttpCreator
 * @Author lb
 * @Date 2022/10/17 9:20
 * @Version 1.0
 */
@Component("http")
public class HttpCreator implements CreatorMethod {


    @Override
    public String create(JobContext context, String[] args) {
        if (args.length == 0) {
            throw new JobException("no url");
        }
        String url = args[0];
        Map<String, Object> body = null;
        if (args.length >= 2) {
            body = JSON.parseObject(args[1], Map.class);
        }
        return HttpUtil.post(url, body, 60000);
    }


}
