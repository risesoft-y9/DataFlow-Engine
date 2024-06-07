package net.risesoft.api.persistence.dao;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.api.persistence.model.IServiceInstanceModel;

import java.util.List;

/**
 * @Description :
 * @ClassName IServiceDao
 * @Author lb
 * @Date 2022/8/3 16:04
 * @Version 1.0
 */
public interface IServiceDao extends Repository {
	
	/**
	 * 根据状态获取
	 *
	 * @param status
	 * @param environment 环境
	 * @return
	 */
	@Search("select * from Y9_DATASERVICE_ISERVICE where status = ?1 and (ENVIRONMENT=?2 or (SERVICE_ID=?3"
			+ " and (select count(*) from Y9_DATASERVICE_ISERVICE where ENVIRONMENT=?2 and SERVICE_ID=?3)=0 ))")
	List<IServiceInstanceModel> findByStatus(Integer status, String environment, String serviceId);

	/**
	 * 根据状态获取
	 *
	 * @param status
	 * @param environment 环境
	 * @return
	 */
	@Search("select INSTANCE_ID from Y9_DATASERVICE_ISERVICE where status = ?1 and ENVIRONMENT=?2 and SERVICE_ID=?3")
	List<String> findByStatusAndService(Integer status, String environment, String serviceId);

	/**
	 * 根据id 查找
	 *
	 * @param id
	 * @return
	 */
	@Search("select * from Y9_DATASERVICE_ISERVICE where instance_Id = ?")
	IServiceInstanceModel findById(String id);

	/**
	 * 更新最近更新时间
	 *
	 * @param id                id
	 * @param currentTimeMillis 时间
	 * @return
	 */
	@Modify("update Y9_DATASERVICE_ISERVICE set UPDATE_TIME = ?2 where INSTANCE_Id = ?1")
	Integer updateNowTime(String id, Long currentTimeMillis);

	/**
	 * 拿到所有服务名
	 *
	 * @return
	 */
	@Search("select SERVICE_Id from Y9_DATASERVICE_ISERVICE where   ENVIRONMENT=? group by SERVICE_Id")
	List<String> getServices(String environment);

	/**
	 * 根据服务名获取服务
	 *
	 * @param name
	 * @return
	 */
	@Search("select * from Y9_DATASERVICE_ISERVICE where SERVICE_Id = ?  and status = 0 and environment =?")
	List<IServiceInstanceModel> getServicesByName(String name, String environment);

	/**
	 * 修改状态
	 *
	 * @param id        id
	 * @param awaitStop 状态
	 * @return
	 */
	@Modify("update Y9_DATASERVICE_ISERVICE set STATUS = ?2 where INSTANCE_Id = ?1")
	Integer updateStatus(String id, Integer awaitStop);

	/**
	 * 获取过期的服务
	 *
	 * @param time
	 * @return
	 */
	@Search("select * from Y9_DATASERVICE_ISERVICE  where expires_time !=0 and expires_time is not null and  status= 0  and expires_time < ? -update_time ")
	List<IServiceInstanceModel> getExpiresService(Long time);

	@Search("select STATUS  from Y9_DATASERVICE_ISERVICE  where INSTANCE_Id = ? ")
	Integer findStatus(String instanceId);

	/**
	 * 查找 排除 获取没有设置监控服务的 获取监控服务中
	 *
	 * @param watchServer 当前监控id
	 * @return
	 */
	@Search("select * from Y9_DATASERVICE_ISERVICE t WHERE WATCH_INFO LIKE " + "'%\\\"watch\\\":true%' "
			+ "AND (WATCH_SERVER != ?1 or WATCH_SERVER is Null) and (ENVIRONMENT = ?2 or ((SELECT count(1) FROM Y9_DATASERVICE_ISERVICE t2 WHERE SERVICE_Id = ?3 and t2.ENVIRONMENT = t.ENVIRONMENT)=0)) and (WATCH_SERVER is null or WATCH_SERVER = '' "
			+ "or WATCH_SERVER NOT IN (SELECT INSTANCE_Id FROM Y9_DATASERVICE_ISERVICE WHERE SERVICE_Id = ?3))")
	List<IServiceInstanceModel> findWatch(String watchServer, String environment, String serviceId);

	/**
	 * 抢占监控
	 *
	 * @param instanceId  修改id
	 * @param watchServer 之前的服务
	 * @param id          id
	 * @return
	 */
	@Modify("update Y9_DATASERVICE_ISERVICE set WATCH_SERVER = ?1 where INSTANCE_Id = ?3 and (WATCH_SERVER=?2 or WATCH_SERVER is null)")
	Integer updateWatch(String instanceId, String watchServer, String id);

	@Search("select INSTANCE_Id from Y9_DATASERVICE_ISERVICE  where WATCH_SERVER=?1 and INSTANCE_Id not in (?2)  ")
	List<String> findMiss(String instanceId, String[] ids);

	@Search("select INSTANCE_Id from Y9_DATASERVICE_ISERVICE  where WATCH_SERVER=?1  ")
	List<String> findMiss(String instanceId);

	@Search("select * from Y9_DATASERVICE_ISERVICE where instance_Id = ? and (status = 0 or status=2) and watch_info is not null and watch_server=?")
	IServiceInstanceModel findWatchById(String id, String instanceId);

	/**
	 * 把不需要我监控的修改掉 oracle 支持一步update
	 * 
	 * @param instanceId
	 * @param ids
	 * @param serviceId
	 * @param environment
	 * @return
	 */
//    @Modify("update	Y9_DATASERVICE_ISERVICE set	WATCH_SERVER = '' where	 INSTANCE_Id in ( select INSTANCE_ID from" + 
//    		" Y9_DATASERVICE_ISERVICE t where WATCH_SERVER =?1 and INSTANCE_Id IN (?2) and ENVIRONMENT != ?4" + 
//    		" and (( select count(1) from Y9_DATASERVICE_ISERVICE t2 where t2.service_Id =?3 and t2.ENVIRONMENT = t.ENVIRONMENT)>0))")
//    Integer updateNoWatch(String instanceId, String[] ids, String serviceId, String environment);
//    
	// mysql 的原因只能拆成2步操作
	@Search("select INSTANCE_ID from"
			+ " Y9_DATASERVICE_ISERVICE t where WATCH_SERVER =?1 and INSTANCE_Id IN (?2) and ENVIRONMENT != ?4"
			+ " and (( select count(1) from Y9_DATASERVICE_ISERVICE t2 where t2.service_Id =?3 and t2.ENVIRONMENT = t.ENVIRONMENT)>0)")
	List<String> searchNoWatch(String instanceId, String[] ids, String serviceId, String environment);

	@Modify("update	Y9_DATASERVICE_ISERVICE set	WATCH_SERVER = '' where	 INSTANCE_Id in ( ?1)")
	Integer updateNoWatch(List<String> instanceIds);
	
	@Search("select * from Y9_DATASERVICE_ISERVICE where status = ?1 and environment = ?2")
	List<IServiceInstanceModel> findAll(Integer status, String environment);
	
	@Search("select * from Y9_DATASERVICE_ISERVICE where environment = ?1")
	List<IServiceInstanceModel> findAll(String environment);

}
