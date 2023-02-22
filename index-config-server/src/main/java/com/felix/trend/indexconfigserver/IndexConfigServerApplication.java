package com.felix.trend.indexconfigserver;

import cn.hutool.core.util.NetUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
@EnableEurekaClient
public class IndexConfigServerApplication {

	public static void main(String[] args) {
		int port = 8060;

		int eurekaServerPort = 8761;
		if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
			System.err.println("eureka server hasn't been launched %n");
			System.exit(1);
		}

		if(!NetUtil.isUsableLocalPort(port)) {
			System.err.printf("port: %d has been occupied，can't launch%n", port );
			System.exit(1);
		}
		new SpringApplicationBuilder(IndexConfigServerApplication.class).properties("server.port=" + port).run(args);

	}
}