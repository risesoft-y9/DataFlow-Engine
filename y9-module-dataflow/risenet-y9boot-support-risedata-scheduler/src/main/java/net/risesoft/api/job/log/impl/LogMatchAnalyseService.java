package net.risesoft.api.job.log.impl;

import net.risesoft.api.persistence.model.log.LogAnalyse;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.api.job.log.LogAnalyseService;
import net.risesoft.api.persistence.model.log.MatchAnalyseModel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description : 通过对日志匹配分析
 * @ClassName LogMatchAnalyseService
 * @Author lb
 * @Date 2023/11/23 10:30
 * @Version 1.0
 */
@Service
public class LogMatchAnalyseService extends AutomaticCrudService<MatchAnalyseModel, String>
		implements LogAnalyseService, ApplicationListener<ApplicationStartingEvent> {

	private Long cacheTime;

	private List<MatchAnalyseModel> matchAnalyseModels;

	@Value("${beta.log.time:3600}")
	private int cacheTimeOut;

	@Override
	public void doAnalyse(Integer jobId, String log, String jobName, List<LogAnalyse> logAnalyseList,
			Map<String, Object> map) {
		// 获取所有的缓存数据60分钟刷新一次
		if (cacheTime == null || System.currentTimeMillis() - cacheTime > cacheTimeOut) {
			this.loadAnalyse();
		}
		StringBuilder msg = new StringBuilder();
		int start;
		for (MatchAnalyseModel matchAnalyseModel : matchAnalyseModels) {
			Matcher matcher = Pattern.compile(matchAnalyseModel.getMatch()).matcher(log);
			// 匹配到对应的值之后开始执行下面逻辑
			if (matcher.find()) {
				start = matcher.end();
				msg.delete(0, msg.length());
				String[] subs = matchAnalyseModel.getSubMatch().split("~~");
				for (int i = 0; i < subs.length; i++) {
					if (isNumeric(subs[i])) {
						// 从上一个位置加
						msg.append(log, start, start + Integer.parseInt(subs[i]) > log.length() ? log.length()
								: start + Integer.parseInt(subs[i]));
						start = start + Integer.parseInt(subs[i]);
					} else {
						Matcher matcher1 = Pattern.compile(subs[i]).matcher(log);
						if (matcher1.find()) {
							// 当匹配的为结尾或当前只有一个正则是使用此处
							if ((i + 1) % 2 == 0 || i == subs.length - 1) {
								if (start == matcher.end()) {
									start = matcher1.start();
								}
								msg.append(log, start, matcher1.end());
							}
							start = matcher1.start();
						}
					}

				}
				logAnalyseList.add(new LogAnalyse(jobId, jobName, matchAnalyseModel.getType(), msg.toString(),
						matchAnalyseModel.getSolution(), Integer.parseInt(map.get("JOB_END_STATUS").toString())));
				break;
			}
		}

	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private synchronized void loadAnalyse() {
		if (cacheTime == null || System.currentTimeMillis() - cacheTime > cacheTime) {
			cacheTime = System.currentTimeMillis();
			matchAnalyseModels = this.searchAll();
		}

	}

	@Override
	public void onApplicationEvent(ApplicationStartingEvent event) {
		cacheTimeOut = cacheTimeOut * 1000;
		System.out.println("设置时间");
	}
}
