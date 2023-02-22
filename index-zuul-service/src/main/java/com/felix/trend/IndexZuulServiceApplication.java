package com.felix.trend;

import brave.sampler.Sampler;
import cn.hutool.core.util.NetUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
@EnableDiscoveryClient
public class IndexZuulServiceApplication {

	public static void main(String[] args) {
		int port = 8031;
		if(!NetUtil.isUsableLocalPort(port)) {
			System.err.printf("port: %d has been occupied, can't launch.%n", port );
			System.exit(1);
		}
		new SpringApplicationBuilder(IndexZuulServiceApplication.class).properties("server.port=" + port).run(args);
	}

	@Bean
	public Sampler defaultSampler() {
		return Sampler.ALWAYS_SAMPLE;
	}

}
