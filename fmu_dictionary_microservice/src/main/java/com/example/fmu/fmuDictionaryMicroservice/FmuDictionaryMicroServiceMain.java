package com.example.fmu.fmuDictionaryMicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FmuDictionaryMicroServiceMain {


	public static void main(String[] args) {
		SpringApplication.run(FmuDictionaryMicroServiceMain.class, args);
	}

}
