package com.example.fmu.fmuImportationMicroservice;

import com.example.fmu.fmuImportationMicroservice.models.InputVariableBloc;
import com.example.fmu.fmuImportationMicroservice.repositories.InputBlocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.HashSet;

@SpringBootApplication
@EnableDiscoveryClient

public class FmuImporationMicroServiceMain {
	public static void main(String[] args) {

		SpringApplication.run(FmuImporationMicroServiceMain.class, args);

	}


}
