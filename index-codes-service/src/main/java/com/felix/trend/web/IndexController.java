package com.felix.trend.web;

import java.util.List;

import com.felix.trend.config.IpConfiguration;
import com.felix.trend.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.felix.trend.pojo.Index;

@RestController
public class IndexController {
	@Autowired
	IndexService indexService;
	@Autowired
	IpConfiguration ipConfiguration;
	
//	http://127.0.0.1:8011/codes
	
	@GetMapping("/codes")
	@CrossOrigin
	public List<Index> codes() throws Exception {
		System.out.println("current instance's port is "+ ipConfiguration.getPort());
		return indexService.get();
	}
}

