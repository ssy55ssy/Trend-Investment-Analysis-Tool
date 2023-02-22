package com.felix.trend.controller;

import com.felix.trend.pojo.IndexData;
import com.felix.trend.service.IndexDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexDataController {

    @Autowired
    IndexDataService indexDataService;

//  http://127.0.0.1:8001/freshCodes
//  http://127.0.0.1:8001/getCodes
//  http://127.0.0.1:8001/removeCodes

    @GetMapping("/freshIndexData/{code}")
    public List<IndexData> fresh(@PathVariable(value = "code") String code) throws Exception {
        return indexDataService.fresh(code);
    }
    @GetMapping("/getIndexData/{code}")
    public List<IndexData> get(@PathVariable(value = "code") String code) throws Exception {
        return indexDataService.get(code);
    }
    @GetMapping("/removeIndexData/{code}")
    public String remove(@PathVariable(value = "code") String code) throws Exception {
        indexDataService.remove(code);
        return "remove codes successfully";
    }

}
