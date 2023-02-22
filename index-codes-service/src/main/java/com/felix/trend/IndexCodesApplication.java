package com.felix.trend;

import brave.sampler.Sampler;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@SpringBootApplication
@EnableEurekaClient
@EnableCaching
public class IndexCodesApplication {
    public static void main(String[] args) {
        int port = 0;
        int defaultPort = 8011;
        int redisPort = 6379;
        int eurekaServerPort = 8761;

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
        
        if(0==port) {
            Future<Integer> future = ThreadUtil.execAsync(() ->{
                int p = 0;
                System.out.printf("please input port number in 5 sec, recommend  %d %n",defaultPort);
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
            port=future.get(5,TimeUnit.SECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e){
            port = defaultPort;
        }        	
        }
        

        if(!NetUtil.isUsableLocalPort(port)) {
            System.err.printf("port:%d has been occpuied，can't launch%n", port );
            System.exit(1);
        }
        new SpringApplicationBuilder(IndexCodesApplication.class).properties("server.port=" + port).run(args);
    	
    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

}
