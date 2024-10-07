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
		System.out.println("-------------- jna devbug load  ------------"+System.setProperty("jna.debug_load", "true"));

		SpringApplication.run(FmuForWinApplication.class, args);
		System.setProperty("jna.library.path", "/native/libs");

		//System.out.println("============env=============="+System.getenv());
		System.out.println("============path systel java lib path =============="+System.getProperty("java.library.path"));





	}
}
