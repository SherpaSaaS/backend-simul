package org.example.fmuWindows;

import java.lang.System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FmuForWinApplication {

	public static void main(String[] args) {
	//	System.setProperty("os.name", "win");
		SpringApplication.run(FmuForWinApplication.class, args);
			System.out.println("=============os name============="+System.getProperty("os.name"));
		//System.out.println("============env=============="+System.getenv());
		//System.out.println("============path=============="+System.getProperty("java.library.path"));





	}
}
