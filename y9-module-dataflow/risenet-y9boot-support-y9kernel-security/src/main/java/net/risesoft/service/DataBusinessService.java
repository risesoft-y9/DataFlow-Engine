package net.risesoft.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import net.risesoft.util.Y9KernelApiUtil;
import net.risesoft.y9.Y9LoginUserHolder;

@Service
public class DataBusinessService {

	/**
	 * 根据ID获取名称
	 * @return
	 */
	public String getNameById(String id) {
		try {
			return Y9KernelApiUtil.getNameById(Y9LoginUserHolder.getTenantId(), id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取全部数据
	 * @return
	 */
	public List<Map<String, Object>> findAll() {
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		try {
			listMap = Y9KernelApiUtil.getDataCatalogTree(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), true);
			listMap.stream().map((item) -> {
	        	if(item.get("parentId") == null) {
	        		item.put("parentId", "0");
	        	}
	            return item;
	        }).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listMap;
	}
	
}
