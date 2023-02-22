package com.felix.trend.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.felix.trend.pojo.IndexData;
import com.felix.trend.util.SpringContextUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "index_dataes")
public class IndexDataService {

    @Autowired
    private RestTemplate restTemplate;
    private Map<String,List<IndexData>> indexDataMap = new HashMap<>();

    //@HystrixCommand(fallbackMethod = "thirdPartNotConnected")
    public List<IndexData> fresh(String code){
        List<IndexData> indexedataes = fetchIndexesDataFromThirdPart(code);
        indexDataMap.put(code,indexedataes);
        IndexDataService indexDataService = SpringContextUtil.getBean(IndexDataService.class);
        indexDataService.remove(code);
        return indexDataService.store(code);
    }

    @CacheEvict(key = "'indexData-code-' + #p0")
    public void remove(String code){
    }

    @CachePut(key = "'indexData-code-' + #p0")
    public List<IndexData> store(String code){
        return indexDataMap.get(code);
    }

    @Cacheable(key="'indexData-code-' + #p0")
    public List<IndexData> get(String code){
        return CollUtil.toList();
    }

    public List<IndexData> fetchIndexesDataFromThirdPart(String code){
        List<Map> result = restTemplate.getForObject("http://127.0.0.1:8090/indexes/" + code + ".json",List.class);
        return mapToIndex(result);
    }

    public List<IndexData> thirdPartNotConnected(String code){
        System.out.println("third_part_not_connected()");
        IndexData indexData= new IndexData();
        indexData.setDate("n/a");
        indexData.setClosePoint(0);
        return CollectionUtil.toList(indexData);
    }

    private List<IndexData> mapToIndex(List<Map> maps){
        List<IndexData> indexDataes = new ArrayList<>();
        for(Map map : maps){
            IndexData indexData = new IndexData();
            indexData.setClosePoint(Convert.toFloat(map.get("closePoint")));
            indexData.setDate(map.get("date").toString());
            indexDataes.add(indexData);
        }
        return indexDataes;
    }

}
