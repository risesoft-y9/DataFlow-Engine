package net.risesoft.api.persistence.iservice.impl;

import net.risedata.jdbc.commons.map.LMap;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risedata.register.model.Const;
import net.risedata.register.service.IServiceInstance;
import net.risedata.register.service.IServiceInstanceFactory;
import net.risesoft.api.listener.ClientListener;
import net.risesoft.api.message.MessageService;
import net.risesoft.api.persistence.dao.IServiceDao;
import net.risesoft.api.persistence.iservice.IServiceService;
import net.risesoft.api.persistence.model.IServiceInstanceModel;
import net.risesoft.api.utils.MapUtils;
import net.risesoft.api.utils.SqlUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

/**
 * @Description : 基于数据库的 databaseService 实现类
 * @ClassName DataBaseIServiceService
 * @Author lb
 * @Date 2022/8/1 17:16
 * @Version 1.0
 */
public class DataBaseIServiceService extends AutomaticCrudService<IServiceInstanceModel, String>
		implements IServiceService {

	private Object saveLock = new Object();

	@Autowired
	MessageService messageService;

	@Override
	public boolean delById(String id) {
		IServiceInstanceModel model = findById(id);
		if (model != null) {
			deleteById(id);
			sendModel(model);
			return true;
		}
		return false;
	}

	@Override
	public boolean instanceClose(String id) {
		IServiceInstanceModel model = findById(id);
		if (model != null && model.getStatus() == IServiceInstance.SUCCESS) {
			deleteById(id);
			sendModel(model);
			return true;
		}
		return false;
	}

	@Override
	public boolean saveModel(IServiceInstanceModel serviceInstance) {
		Integer status = iServiceDao.findStatus(serviceInstance.getInstanceId());
		boolean res = false;
		if (status == null) {
			synchronized (saveLock) {
				try {
					status = iServiceDao.findStatus(serviceInstance.getInstanceId());
					if (status == null) {
						if (serviceInstance.getWatchServer() == null) {
							serviceInstance.setWatchServer("");
						}
						insert(serviceInstance);
					}
				} catch (Exception e) {
				}
			}
		} else {
			if (status == IServiceInstance.AWAIT_STOP) {
				serviceInstance.setStatus(IServiceInstance.SUCCESS);
			} else {
				serviceInstance.setStatus(status);
			}
			updateById(serviceInstance);
			res = true;
		}
		sendModel(serviceInstance);

		return res;
	}

	private boolean sendModel(IServiceInstanceModel serviceInstance) {
		Map<String, Object> map = new HashMap<>();
		if (serviceInstance.getStatus() == IServiceInstance.SUCCESS) {
			map.put("instance", serviceInstance);
			map.put("environment", serviceInstance.getEnvironment());
			ClientListener.pushListener(Const.REGISTER_LISTENER, map, null, null, serviceInstance.getEnvironment());
			return true;
		} else {
			if (serviceInstance.getStatus() != IServiceInstance.AWAIT_STOP) {
				messageService.onServiceDown(serviceInstance);
			}
			map.put("serviceId", serviceInstance.getServiceId());
			map.put("instanceId", serviceInstance.getInstanceId());
			map.put("environment", serviceInstance.getEnvironment());
			ClientListener.pushListener(Const.REMOVE_LISTENER, map, null, null, serviceInstance.getEnvironment());
			return false;
		}
	}

	private boolean sendRemove(IServiceInstanceModel serviceInstance) {
		Map<String, Object> map = new HashMap<>();
		map.put("serviceId", serviceInstance.getServiceId());
		map.put("instanceId", serviceInstance.getInstanceId());
		map.put("environment", serviceInstance.getEnvironment());
		ClientListener.pushListener(Const.REMOVE_LISTENER, map, null, null, serviceInstance.getEnvironment());
		return false;

	}

	@Autowired
	IServiceDao iServiceDao;

	@Autowired
	IServiceInstanceFactory iServiceInstanceFactory;

	@Override
	public Map<String, List<IServiceInstanceModel>> getUseAll(String environment) {
		return MapUtils.createMaps(
				iServiceDao.findByStatus(IServiceInstance.SUCCESS, environment, iServiceInstanceFactory.getIsntance().getServiceId()),
				(IServiceInstanceModel is) -> is.getServiceId());
	}

	@Override
	public IServiceInstanceModel findById(String id) {
		return iServiceDao.findById(id);
	}

	@Override
	public IServiceInstanceModel findWatchById(String id) {
		return iServiceDao.findWatchById(id, iServiceInstanceFactory.getIsntance().getInstanceId());
	}

	@Override
	public Integer updateNowTime(String id) {
		return iServiceDao.updateNowTime(id, System.currentTimeMillis());
	}

	@Override
	public List<String> getServices(String environment) {

		return iServiceDao.getServices(environment);
	}

	@Override
	public List<IServiceInstanceModel> getService(String name,String environment) {
		return iServiceDao.getServicesByName(name,environment);
	}

	@Override
	public boolean setStatus(String id, int status) {
		boolean res = iServiceDao.updateStatus(id, status) == 0 ? false : true;
		if (res) {
			sendModel(findById(id));
		}
		return res;
	}

	@Override
	public List<IServiceInstanceModel> findWatch(String watchServer, String environment, String serviceId) {
		return iServiceDao.findWatch(watchServer, environment, serviceId);
	}

	@Override
	public boolean updateWatch(String instanceId, String watchServer, String id) {
		return iServiceDao.updateWatch(instanceId, watchServer, id) > 0;
	}

	@Override
	public List<String> findMiss(String instanceId, String[] ids) {
		if (ids.length < 1) {
			return iServiceDao.findMiss(instanceId);
		}
		return iServiceDao.findMiss(instanceId, ids);
	}

	@Override
	public Integer updateNoWatch(String[] ids) {
		if (ids == null || ids.length == 0) {
			return 0;
		}
		List<String> updatedIds = iServiceDao.searchNoWatch(iServiceInstanceFactory.getIsntance().getInstanceId(), ids,
				iServiceInstanceFactory.getIsntance().getServiceId(),
				iServiceInstanceFactory.getIsntance().getEnvironment());
		if (updatedIds.isEmpty()) {
			return 0;
		}
		return iServiceDao.updateNoWatch(updatedIds);
	}

	@Override
	public List<String> getUseAllSearch(String environment, String service) {
		return iServiceDao.findByStatusAndService(IServiceInstance.SUCCESS, environment, service);
	}

	@Override
	public List<String> getAllForName(String environment, String[] services) {

		return getSearchExecutor().searchForList(IServiceInstanceModel.class, "distinct SERVICE_Id",
				SqlUtils.createInlikeOperaion("serviceId", "serviceId", services, false),
				LMap.createMap("environment", environment), null, String.class);

	}

	// 处理监控任务

	@Value("${beta.service.checkEnable:false}")
	private boolean isExpire;

	/**
	 * 检查过期的服务 30 秒 默认 30000
	 */
	@Scheduled(fixedDelayString = "${beta.service.check:30000}")
	public void checkService() {
		if (!isExpire) {
			return;
		}
		// TODO 这个功能异常
		List<IServiceInstanceModel> expiresService = iServiceDao.getExpiresService(System.currentTimeMillis());
		if (expiresService.size() > 0) {
			for (IServiceInstanceModel iServiceInstanceModel : expiresService) {
				setStatus(iServiceInstanceModel.getInstanceId(), IServiceInstance.ERROR);
			}
		}
	}

	@Override
	public List<IServiceInstanceModel> findAll(Integer status, String environment) {
		if(status == null) {
			return iServiceDao.findAll(environment);
		}
		return iServiceDao.findAll(status, environment);
	}

}
