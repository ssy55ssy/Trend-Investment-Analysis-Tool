package com.felix.trend;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableHystrix
@EnableCaching
public class IndexGatherStoreServiceApplication {

	public static void main(String[] args) {
		int port = 0;
		int defaultPort = 8001;
		int eurekaServerPort = 8761;
		int redisPort = 6379;
		port = defaultPort ;

		if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
			System.err.println("eureka server hasn't been started");
			System.exit(1);
		}
		if(NetUtil.isUsableLocalPort(redisPort)) {
			System.err.println("redis server hasn't been started");
			System.exit(1);
		}

		if(null!=args && 0!=args.length) {
			for (String arg : args) {
				if(arg.startsWith("port=")) {
					String strPort= StrUtil.subAfter(arg, "port=", true);
					if(NumberUtil.isNumber(strPort)) {
						port = Convert.toInt(strPort);
					}
				}
			}
		}

		if(!NetUtil.isUsableLocalPort(port)) {
			System.err.printf("port:%d has been occpuied，can't launch%n", port );
			System.exit(1);
		}
		new SpringApplicationBuilder(IndexGatherStoreServiceApplication.class).properties("server.port=" + port).run(args);
	}

	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

}
