package org.example.fmuWindows;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FmuTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(FmuTestApplication.class, args);
	}

}
