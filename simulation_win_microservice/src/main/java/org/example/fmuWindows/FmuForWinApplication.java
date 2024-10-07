package org.example.fmuWindows;

import java.lang.System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FmuForWinApplication {

	public static void main(String[] args) {
		System.setProperty("os.name", "win");

		SpringApplication.run(FmuForWinApplication.class, args);
		System.out.println("-------------- jna devbug load  ------------"+System.setProperty("jna.debug_load", "true"));
		System.setProperty("jna.debug_load.jna", "true");
		System.out.println("-------------- jna devbug load  ------------"+System.getProperty("jna.debug_load"));

		//System.out.println("============env=============="+System.getenv());
		System.out.println("============path  java lib path =============="+System.getProperty("java.library.path"));
		System.out.println("============path  jna lib path =============="+System.getProperty("jna.library.path"));
		//System.load(ClassLoader.getSystemClassLoader().getResource("win32-x86-64/libjnidispatch.so").getPath());

		//System.out.println("============ class loader  =============="+ClassLoader.getSystemClassLoader().getResource("win32-x86-64/libjnidispatch.so").getPath());
		//System.out.println("============== Classpath: " + System.getProperty("java.class.path"));



	}
}
