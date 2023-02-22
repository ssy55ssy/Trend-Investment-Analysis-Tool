package com.felix.trend.thirdpartindexdataproject;

import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ThirdPartIndexDataProjectApplication {

	public static void main(String[] args) {
		int port = 8090;
		int eurekaServerPort = 8761;
		if(NetUtil.isUsableLocalPort(eurekaServerPort)){
			System.err.println("Eureka Server hasn't been started!");
			System.exit(1);
		}
		if(args!=null && args.length != 0){
			for(String arg : args){
				if(arg.startsWith("port=")){
					String strPort = StrUtil.subAfter(arg,"port=",true);
					if(NumberUtil.isNumber(strPort))
						port = Integer.parseInt(strPort);
				}
			}
		}
		if(!NetUtil.isUsableLocalPort(port)){
			System.err.printf("port: %d has been started! %n",port);
		}
		new SpringApplicationBuilder(ThirdPartIndexDataProjectApplication.class).properties("server.port=" + port).run(args);
	}

}
