package net.risesoft.api.job.creator.impl;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONArray;
import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.actions.dispatch.method.DispatchActionManager;
import net.risesoft.api.job.creator.CreatorMethod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description : 对于args #{args:true/false} 当为true的时候为分割
 * @ClassName DispatchArgsCreator
 * @Author lb
 * @Date 2022/10/17 10:08
 * @Version 1.0
 */
@Component("args")
public class DispatchArgsCreator implements CreatorMethod {

    @Override
    public String create(JobContext context, String[] args) {
        boolean isSplit = args.length > 0 ? Boolean.valueOf(args[0]) : false;
        if (isSplit) {
            Object value = context.getArgs().get(DispatchActionManager.DISPATCH_ARGS);
            if (value instanceof JSONArray) {

                int index = (Integer) context.getRequiredValue("index");
                JSONArray arrays = ((JSONArray) value);
                List<Object> res = new ArrayList<>();
                int maxSize = (Integer) context.getArgs().get(DispatchActionManager.INSTANCE_SIZE);

                while (arrays.size() > index) {
                    res.add(arrays.get(index));
                    index += maxSize;
                }

                return ArrayUtil.join(res.toArray(), ",");
            } else {
                throw new JobException("需要拆分的类型不为array");
            }
        }

        return context.getArgs().get(DispatchActionManager.DISPATCH_ARGS).toString();
    }

}
