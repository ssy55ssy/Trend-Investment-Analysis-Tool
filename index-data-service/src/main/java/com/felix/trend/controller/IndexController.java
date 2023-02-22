package com.felix.trend.controller;

import com.felix.trend.configuration.IpConfiguration;
import com.felix.trend.pojo.IndexData;
import com.felix.trend.service.IndexDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @Autowired
    IndexDataService indexDataService;

    @Autowired
    IpConfiguration ipConfiguration;

    @GetMapping("/data/{code}")
    public List<IndexData> get(@PathVariable("code") String code) throws Exception{
        System.out.println("current port: " + ipConfiguration.getPort());
        return indexDataService.get(code);
    }

}
