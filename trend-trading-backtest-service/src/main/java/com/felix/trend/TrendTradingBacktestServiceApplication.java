package com.felix.trend;

import brave.sampler.Sampler;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
@EnableCircuitBreaker
public class TrendTradingBacktestServiceApplication {

	public static void main(String[] args) {
		int port = 0;
		int defaultPort = 8051;
		int eurekaServerPort = 8761;

		if(NetUtil.isUsableLocalPort(eurekaServerPort)) {
			System.err.println("eureka server hasn't been launched");
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

		if(0==port) {
			Future<Integer> future = ThreadUtil.execAsync(() ->{
				int p = 0;
				System.out.printf("please input port number in 5 sec, recommend:  %d %n ",defaultPort);
				Scanner scanner = new Scanner(System.in);
				while(true) {
					String strPort = scanner.nextLine();
					if(!NumberUtil.isInteger(strPort)) {
						System.err.println("can only be number");
						continue;
					}
					else {
						p = Convert.toInt(strPort);
						scanner.close();
						break;
					}
				}
				return p;
			});
			try{
				port=future.get(5, TimeUnit.SECONDS);
			}
			catch (InterruptedException | ExecutionException | TimeoutException e){
				port = defaultPort;
			}
		}

		if(!NetUtil.isUsableLocalPort(port)) {
			System.err.printf("port: %d has been occupied，can't launch %n", port );
			System.exit(1);
		}
		new SpringApplicationBuilder(TrendTradingBacktestServiceApplication.class).properties("server.port=" + port).run(args);

	}

	@Bean
	public Sampler defaultSampler() {
		return Sampler.ALWAYS_SAMPLE;
	}

	@Bean
	public ServletRegistrationBean getServlet() {
		HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("/hystrix.stream");
		registrationBean.setName("HystrixMetricsStreamServlet");
		return registrationBean;
	}

}
