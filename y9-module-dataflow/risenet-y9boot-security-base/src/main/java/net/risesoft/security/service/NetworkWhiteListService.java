package net.risesoft.security.service;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.security.model.NetworkWhiteList;

import java.util.List;

/**
 * @Description : 网络请求白名单操作类
 * @ClassName NetworkWhiteListService
 * @Author lb
 * @Date 2022/8/8 10:40
 * @Version 1.0
 */
public interface NetworkWhiteListService {
	/**
	 * 保存
	 * 
	 * @param network
	 */
	void saveNetworkWhiteList(NetworkWhiteList network);

	/**
	 * 删除一条白名单设置
	 * 
	 * @param id
	 */
	void delById(String id);

	/**
	 * 分页查询
	 * 
	 * @param network
	 * @param page
	 * @return
	 */
	LPage<NetworkWhiteList> searchByNetworkWhiteList(NetworkWhiteList network, LPageable page);

	/**
	 * 更具缓存获取白名单信息
	 * 
	 * @param environment
	 * @return
	 */
	List<NetworkWhiteList> getNetworkWhiteList(String environment);

	/**
	 * 根据ip 获取满足的授权列表
	 * 
	 * @param networkWhiteLists
	 * @param ipAddress
	 * @return
	 */
	List<NetworkWhiteList> getNetworkWhiteListFotIP(List<NetworkWhiteList> networkWhiteLists, String ipAddress);


	/**
	 * 拿到可以注册的服务
	 * 
	 * @param networkWhiteLists
	 * @return
	 */
	List<String> joinService(List<NetworkWhiteList> networkWhiteLists);

	/**
	 * 拿到缓存中的权限
	 * 
	 * @param ipMatch
	 * @return
	 */
	String[] getStrs(String ipMatch);

	/**
	 * 根据id获取
	 * 
	 * @param id
	 * @return
	 */
	NetworkWhiteList findById(String id);
	
	List<NetworkWhiteList> findAll();
}
