package com.cloud.server.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class CloudConfigCustomSvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudConfigCustomSvcApplication.class, args);
	}

}
