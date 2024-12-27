package net.risesoft.service;

import org.springframework.stereotype.Component;

import net.risesoft.pojo.Y9Result;

@Component
public class JobDoActionService {
	
	/**
	 * 执行任务操作
	 * @param body
	 * @return
	 */
	public Y9Result<String> doAction(String body) {
		return Y9Result.success("开源版本不支持该功能，有需要请联系作者");
	}

}
