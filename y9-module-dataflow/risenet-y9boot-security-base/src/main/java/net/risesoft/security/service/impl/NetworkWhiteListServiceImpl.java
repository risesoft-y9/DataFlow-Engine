package net.risesoft.security.service.impl;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risesoft.security.model.NetworkWhiteList;
import net.risesoft.security.service.NetworkWhiteListService;
import net.risesoft.util.AutoIdUtil;
import net.risesoft.util.PattenUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description : 网络操作白名单
 * @ClassName NetworkWhiteListServiceImpl
 * @Author lb
 * @Date 2022/8/8 10:45
 * @Version 1.0
 */
@Service
public class NetworkWhiteListServiceImpl extends AutomaticCrudService<NetworkWhiteList, String> implements NetworkWhiteListService {
	
    @Override
    public void saveNetworkWhiteList(NetworkWhiteList network) {
        if (StringUtils.isEmpty(network.getId())) {
            network.setId(AutoIdUtil.getRandomId26());
            network.setCreateDate(new Date());
            insert(network);
        } else {
            updateById(network);
        }
    }

    @Override
    public void delById(String id) {
        deleteById(id);
    }

    @Override
    public LPage<NetworkWhiteList> searchByNetworkWhiteList(NetworkWhiteList network, LPageable page) {
        return searchForPage(network, page);
    }

    private Map<String, List<NetworkWhiteList>> environmentMap;

    private Map<String, String[]> cacheMap;

    @Override
    public List<NetworkWhiteList> getNetworkWhiteList(String environment) {
        load();
        return environmentMap.get(environment);
    }

    @Override
    public List<NetworkWhiteList> getNetworkWhiteListFotIP(List<NetworkWhiteList> networkWhiteLists, String ipAddress) {
        List<NetworkWhiteList> lists = new ArrayList<>();
        for (NetworkWhiteList networkWhiteList : networkWhiteLists) {
            if (PattenUtil.hasMatch(networkWhiteList.getIpMatch(), ipAddress)) {
                lists.add(networkWhiteList);
            }
        }
        return lists;
    }


    @Override
    public List<String> joinService(List<NetworkWhiteList> networkWhiteLists) {
        List<String> service = new ArrayList<>();
        for (NetworkWhiteList networkWhiteList : networkWhiteLists) {
            String[] split = networkWhiteList.getService().split(",");
            for (String s : split) {
                service.add(s);
            }
        }
        return service;
    }

    @Override
    public String[] getStrs(String ipMatch) {
        String[] res = cacheMap.get(ipMatch);
        if (res == null) {
            res = ipMatch.split(",");
            cacheMap.put(ipMatch, res);
        }
        return res;
    }

    /**
     * 上一次使用时间
     */
    private long dataDate;
    /**
     * 缓存有效期 默认5小时 单位分钟
     */
    @Value("${beta.whitelist.time:300}")
    private int validityDate;

    private synchronized void load() {
        if (this.environmentMap == null || (System.currentTimeMillis() - dataDate) > (validityDate * 60 * 1000)) {
            environmentMap = new HashMap<>();
            cacheMap = new HashMap<>();
            List<NetworkWhiteList> networkWhiteLists = searchAll();
            for (NetworkWhiteList networkWhiteList : networkWhiteLists) {
                List<NetworkWhiteList> networkWhiteLists1 = environmentMap.get(networkWhiteList.getEnvironment());
                if (networkWhiteLists1 == null) {
                    networkWhiteLists1 = new ArrayList<>();
                    environmentMap.put(networkWhiteList.getEnvironment(), networkWhiteLists1);
                }
                networkWhiteLists1.add(networkWhiteList);
            }
            dataDate = System.currentTimeMillis();
        }
    }

	@Override
	public NetworkWhiteList findById(String id) {
		return getOne(id);
	}

	@Override
	public List<NetworkWhiteList> findAll() {
		return searchAll();
	}

}
