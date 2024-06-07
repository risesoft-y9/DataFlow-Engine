package net.risesoft.api.persistence.iservice;

import net.risesoft.api.persistence.model.IServiceInstanceModel;

import java.util.List;
import java.util.Map;

/**
 * @Description : 作为 持久化iservice的 接口
 * @ClassName IServiceService
 * @Author lb
 * @Date 2022/8/1 15:50
 * @Version 1.0
 */
public interface IServiceService {
    /**
     * 根据 id 删除实例
     *
     * @param id
     */
    boolean delById(String id);
    /**
     * 根据 id 删除实例
     *
     * @param id
     */
    boolean instanceClose(String id);
    /**
     * 保存一个实例
     */
    boolean saveModel(IServiceInstanceModel serviceInstance);

    /**
     * 拿到可用的所有服务
     *
     * @return
     */
    Map<String, List<IServiceInstanceModel>> getUseAll(String environment);

    /**
     * 根据id 获取
     * @param id
     * @return
     */
    IServiceInstanceModel findById(String id);
    /**
     * 查找增加监控过滤
     * @param id
     * @return
     */
    IServiceInstanceModel findWatchById(String id);
    /**
     * 修改最近更新时间
     * @param id
     * @return
     */
    Integer updateNowTime(String id);

    /**
     * 拿到所有服务 名
     * @return
     */
    List<String> getServices(String environment);

    /**
     * 根据服务名获取
     * @param name
     * @return
     */
    List<IServiceInstanceModel> getService(String name,String environment);

    /**
     * 修改状态
     * @param id
     * @param status
     * @return
     */
    boolean setStatus(String id, int status);

    List<IServiceInstanceModel> findWatch( String watchServer,String environment,String serviceId);

    /**
     * 修改监控信息
     * @param instanceId 修改的id
     * @param watchServer 条件服务
     * @return
     */
    boolean updateWatch(String instanceId, String watchServer,String id);

    /**
     * 查找不存在的
     * @param instanceId 当前实例id
     * @param ids key 集合
     * @return
     */
    List<String> findMiss(String instanceId, String[] ids);

    /**
     * 将不属于该服务器监控的给update掉
     * @param ids
     */
    Integer updateNoWatch(String[] ids);

    List<String> getUseAllSearch(String environment, String service);

    List<String> getAllForName(String environment, String[] services);
    
    List<IServiceInstanceModel> findAll(Integer status, String environment);
}
