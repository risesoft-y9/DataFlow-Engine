package net.risesoft.controller.register;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.risedata.register.config.ListenerConfigs;
import net.risedata.register.service.IServiceInstance;
import net.risedata.rpc.provide.net.ClinetConnection;
import net.risesoft.api.listener.ClientListener;
import net.risesoft.api.persistence.iservice.IServiceService;
import net.risesoft.api.persistence.model.IServiceInstanceModel;
import net.risesoft.api.watch.CheckStatusTask;
import net.risesoft.api.watch.WatchManager;
import net.risesoft.controller.BaseController;
import net.risesoft.pojo.Y9Result;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/api/rest/system/service")
public class RegisterController extends BaseController {
	@Autowired()
	IServiceService iServiceService;

	/**
	 * 修改状态
	 * 
	 * @param serviceId
	 * @param id
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus/{id}/{status}")
	public boolean updateStatus(@PathVariable String id, @PathVariable int status) {
		return iServiceService.setStatus(id, status);
	}

	/**
	 * 重启
	 * 
	 * @param serviceId
	 * @param id
	 * @return
	 */
	@PostMapping("/reStart/{id}")
	public Object reStart(@PathVariable String id) {
		ClinetConnection connection = ClientListener.getConnection(id);
		if (connection != null) {
			iServiceService.setStatus(id, IServiceInstance.AWAIT_STOP);
			JSONArray json = connection.pushListener(ListenerConfigs.RE_START, new HashMap<>(), 10000).getResult()
					.getValue();
			System.out.println("重启返回值" + json);
			if (json == null || json.size() == 0) {
				return -1;
			}
			return json.getInteger(0);
		}
		return -1;
	}

	/**
	 * 传入 instanceids 对指定的服务器执行事件请求 webflux
	 *
	 * @param instanceIds ids集合
	 * @param operation   操作
	 * @param argsJson    参数
	 * @return
	 */
	@PostMapping("/sendOp")
	public Mono<Object> sendOp(@RequestParam(required = true) String[] instanceIds,
			@RequestParam(required = true) String operation, @RequestParam(required = true) String argsJson) {

		return Mono.create((succ) -> {
			ClinetConnection clinetConnection;
			Map<String, Object> args = JSONObject.parseObject(argsJson);
			Map<String, Object> res = new HashMap<>();
			AtomicInteger count = new AtomicInteger(instanceIds.length);
			try {
				for (String instanceId : instanceIds) {
					clinetConnection = ClientListener.getConnection(instanceId);
					if (clinetConnection == null) {
						res.put(instanceId, -1);
						if (count.decrementAndGet() == 0) {
							res.put("status", 200);
							succ.success(res);
						}
					} else {
						clinetConnection.pushListener(operation, args, 10000).onSuccess((result) -> {
							if (result.getValue() != null) {
								res.put(instanceId, result.getValue().get(0));
							} else {
								res.put(instanceId, -1);
							}
							if (count.decrementAndGet() == 0) {
								res.put("status", 200);
								succ.success(res);
							}
						}).onError((request, e) -> {

							e.printStackTrace();
							res.put(instanceId, "error:" + e.getMessage());
							if (count.decrementAndGet() == 0) {
								res.put("status", 200);
								succ.success(res);
							}
						});
					}
				}
			} catch (Exception e) {
				res.put("status", 500);
				res.put("error", e.getMessage());
				succ.success(res);
			}
		});
	}

	/**
	 * 获取监控
	 * 
	 * @return
	 */
	@RequestMapping("/getWatchs")
	public Map<String, CheckStatusTask> getWatchs() {
		return WatchManager.getWatchTask();
	}

	/**
	 * 根据id删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("remove")
	public boolean removeServer(String id) {
		iServiceService.delById(id);
		return true;
	}

	// 发送事件执行操作
	@RequestMapping("/execute/{operation}/{id}")
	public Object execute(@PathVariable String operation, @PathVariable String id) {
		ClientListener.pushListener(operation, new HashMap<>(), null, id, null);
		return true;
	}

	/**
	 * 获取所有服务
	 * 
	 * @param environment
	 * @return
	 */
	@RequestMapping("/getServicesAll")
	public List<IServiceInstanceModel> getServicesAll(
			@RequestParam(required = false, defaultValue = "Public") String environment, Integer status) {
		return iServiceService.findAll(status, environment);

	}

	/**
	 * 根据实例id获取所有可用的实例
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("/getServiceByName")
	public Y9Result<List<IServiceInstanceModel>> getServiceByName(@RequestParam String name,@RequestParam(required = false, defaultValue = "Public") String environment) {
		name = name.toUpperCase();
		return Y9Result.success(iServiceService.getService(name,environment));
	}

	/**
	 * 检查服务
	 *
	 * @param serviceInstance
	 * @param secure
	 * @return
	 */
	@PostMapping("/check")
	public Y9Result<Object> check(String id) {
		IServiceInstanceModel model = iServiceService.findById(id);
		return Y9Result.success(CheckStatusTask.check(model));
	}

}
