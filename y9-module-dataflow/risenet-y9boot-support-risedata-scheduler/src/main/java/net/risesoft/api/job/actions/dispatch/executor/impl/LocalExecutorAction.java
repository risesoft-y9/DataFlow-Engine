package net.risesoft.api.job.actions.dispatch.executor.impl;

import net.risedata.rpc.provide.listener.SyncResult;
import net.risedata.rpc.provide.net.ClinetConnection;
import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.actions.dispatch.ExecutorAction;
import net.risesoft.api.job.actions.dispatch.executor.DoBalance;
import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.job.actions.dispatch.executor.ResultError;
import net.risesoft.api.job.actions.dispatch.executor.ResultSuccess;
import net.risesoft.api.job.actions.dispatch.method.DispatchActionManager;
import net.risesoft.api.listener.ClientListener;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.function.impl.StringToAny;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description : 本地方法调用type=local,args=arg1&&arg2,source=beanName,methodName
 * @ClassName LocalExecutorAction
 * @Author lb
 * @Date 2024/11/12 11:12
 * @Version 1.0
 */
@Component("local")
public class LocalExecutorAction implements ExecutorAction {

	@Autowired
	ApplicationContext ac;

	@Override
	public Result action(Job job, JobLog jobLog, Map<String, Object> args, ServiceInstance iServiceInstance,
			JobContext jobContext, DoBalance doBalance) {
		Object arg = args.get("args");
		if (arg == null || StringUtils.isEmpty(job.getSource())) {
			throw new RuntimeException("缺失必要的参数!");
		}

		String[] strArgs = job.getSource().split(",");
		;
		if (strArgs.length < 2) {
			throw new RuntimeException("缺失必要的参数!");
		}
		Object beanObject = ac.getBean(strArgs[0]);
		Method[] methods = beanObject.getClass().getMethods();
		Method method = null;
		for (Method m : methods) {
			if (m.getName().equals(strArgs[1])) {
				method = m;
				break;
			}
		}
		if (method == null) {
			throw new RuntimeException(strArgs[1] + " 未找到此方法!");
		}
		strArgs = ((String) arg).split("&&");
		Parameter[] ps = method.getParameters();
		Object[] invokeArgs = new Object[ps.length];
		if (strArgs.length!=ps.length) {
			throw new RuntimeException("参数长度与实际所需长度不一致");
		}
		for (int i = 0; i < ps.length; i++) {
			if(ps[i].getType()==String.class) {
				invokeArgs[i]=strArgs[i];
			}else {
				invokeArgs[i] = new StringToAny(ps[i].getType(), "").apply(strArgs[i]);
			}
		}
		Exception exc = null;
		Object returnValue = null;
		try {
			returnValue = method.invoke(beanObject, invokeArgs);
		} catch (Exception e) {
			exc = e;
		}
		final Exception exception = exc;
		final Object returnValueObject = returnValue;
		return new Result() {

			@Override
			public Result onSuccess(ResultSuccess success) {
				if (returnValueObject != null) {
					success.onSuccess(returnValueObject);

				}
				return this; 
			}

			@Override
			public Result onError(ResultError error) {
				if (exception != null) {
					error.onError(exception);
				}
				return this;
			}

			@Override
			public Object getValue() {
				if (exception != null) {
					throw new RuntimeException(exception);
				}
				return returnValueObject;
			}
		};
	}
}
