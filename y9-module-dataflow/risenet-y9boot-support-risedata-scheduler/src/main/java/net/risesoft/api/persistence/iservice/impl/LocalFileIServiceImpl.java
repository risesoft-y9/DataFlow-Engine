package net.risesoft.api.persistence.iservice.impl;

import cn.hutool.core.io.FileUtil;
import net.risedata.register.service.IServiceInstance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 随着版本迭代此类已经舍去
 * @Description : 已本地文件作为存储数据的Service
 * @ClassName LocalFileIServiceImpl
 * @Author lb
 * @Date 2022/8/1 16:05
 * @Version 1.0
 */
public class LocalFileIServiceImpl  {

    private Map<String, IServiceInstance> instanceMap = new ConcurrentHashMap<>();
    @Value("${register.discovery.repository:}")
    String repository;

    public void toFile() {
        saveInfo(JSON.toJSONString(instanceMap));
    }

    public void saveInfo(String info) {
        if (!StringUtils.isEmpty(repository)) {
            FileUtil.writeString(info, new File(repository), "UTF-8");
        }
    }


    public void delById(String id) {
        if (instanceMap.remove(id) != null) {
            toFile();
        }
    }


    public void save(IServiceInstance serviceInstance) {
        instanceMap.put(serviceInstance.getInstanceId(), serviceInstance);
        toFile();
    }


    public List<IServiceInstance> getAll() {
        List<IServiceInstance> iServiceInstances = new ArrayList<>();
        String strs = FileUtil.readUtf8String(repository);
        if (!StringUtils.isEmpty(strs)) {
            JSONObject datas = JSON.parseObject(strs);
            IServiceInstance iServiceInstance;
            for (String key : datas.keySet()) {
                iServiceInstance = datas.getObject(key, IServiceInstance.class);
                instanceMap.put(key, iServiceInstance);
                iServiceInstances.add(iServiceInstance);
            }
        }
        return iServiceInstances;
    }
}
