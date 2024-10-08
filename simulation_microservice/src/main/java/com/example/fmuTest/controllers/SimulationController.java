package com.example.fmuTest.controllers;

import com.example.fmuTest.dtos.GetFmuSimulationInfoResponse;
import com.example.fmuTest.dtos.PrioritizeSimulationVariableInputEventDto;
import com.example.fmuTest.dtos.PrioritizeVariableEventDto;
import com.example.fmuTest.eventPublishers.PrioritizeVariableEventPublisher;
import com.example.fmuTest.models.VariablesValues;
import com.example.fmuTest.services.CsvScenarioService;
import com.example.fmuTest.services.DataToCSVService;

import com.example.fmuTest.models.VariablePrioritizerMap;
import com.example.fmuTest.models.FileValidationResult;
import com.example.fmuTest.services.FmuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/api/simulation")
public class SimulationController {
    @Autowired
    FmuService fmu;


    @Autowired
    DataToCSVService excelDataExtractorService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PrioritizeVariableEventPublisher publisher;

    @Autowired
    private DiscoveryClient client;

    @Autowired
    CsvScenarioService csvScenarioService;

    @PostMapping("/withScenario/{id}")
    public ResponseEntity startSimulationwithScenario(HttpServletRequest request , @RequestParam(name = "file", required = false) MultipartFile file, @PathVariable("id") Integer fmuId) throws IOException {
       // call the getInputVariableList function to extract the input variables of the fmu
        List<String> inputVariableList = csvScenarioService.getInputVariableList(fmuId);

        ResponseEntity<FileValidationResult> response = csvScenarioService.checkFile(file);
        if (response.getStatusCode() == HttpStatus.OK) {
            // Access the file path using the FileValidationResult object
            String filePath = response.getBody().getMessage();

            VariablesValues csvData = csvScenarioService.getReadData(filePath);
            // call the searchInputs function to return only the input variables exist in the fmu
            List<String> filterInputs = csvScenarioService.filterInputs(csvData.getVariableNames(), inputVariableList);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            header.set(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
            header.setContentType(MediaType.APPLICATION_JSON);
            header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity requestToSend = new HttpEntity(header);


            // get ServiceInstance list using serviceId
            List<ServiceInstance> siList = client.getInstances("fmu-importation-ms");

            // read manually one instance from index#0
            ServiceInstance si = siList.get(0);

            // read URI and Add path that returns url
            String url = si.getUri()+"/api/variable/getVariable/" + fmuId;


            ResponseEntity<GetFmuSimulationInfoResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestToSend, GetFmuSimulationInfoResponse.class);
            VariablePrioritizerMap.addFmuEntryInMap(fmuId);

            fmu.runWithScenario(responseEntity.getBody().getVariableDtoList(),
                    filterInputs.toArray(String[]::new), //convert list<String> to String[]
                    csvData.getVariableNames(),
                    csvData.getValues(),
                    Double.parseDouble(csvData.getValues().get(0)[0]), //initial value
                    Double.parseDouble(csvScenarioService.extractFinalValue(csvData.getValues(), fmuId)),
                    fmuId);

            VariablePrioritizerMap.removeFmuEntryFromMap(fmuId);
            // close readers

            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } else {
            // Handle validation errors using the error message in FileValidationResult
            String errorMessage = response.getBody().getMessage();
            return ResponseEntity.status(response.getStatusCode()).body(errorMessage);
        }
    }


    @PostMapping("/default/{id}")
    public ResponseEntity<GetFmuSimulationInfoResponse> testSimulation(HttpServletRequest request , @PathVariable("id") Integer fmuId) {
        ResponseEntity<GetFmuSimulationInfoResponse> responseEntity = null;

        try {
            //to call the variableController of the importation microService
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders header = new HttpHeaders();
            header.set(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));
            header.setContentType(MediaType.APPLICATION_JSON);
            header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
           // header.set("Platform","linux");

            HttpEntity requestToSend = new HttpEntity(header);

            // get ServiceInstance list using serviceId
            List<ServiceInstance> siList = client.getInstances("fmu-importation-ms");

            // read manually one instance from index#0
            ServiceInstance si = siList.get(0);
        //    System.out.println("------------------instance    --------------------"+si.getUri());

            // read URI and Add path that returns url
            String url = si.getUri()+"/api/variable/getVariable/" + fmuId;

          responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestToSend, GetFmuSimulationInfoResponse.class);
           // System.out.println("-----------dataaa ---------------- : "+responseEntity.getBody());

            VariablePrioritizerMap.addFmuEntryInMap(fmuId);
           // System.load("/tmp/fmu_39343501_d8c9_4891_b9dc_8892c33fae59PTF_FC_V8p0_R2022b/binaries/linux64/PTF_FC_V8p0_R2022b.so");
            fmu.runDefaultSimulation(responseEntity.getBody().getFmuId(), responseEntity.getBody().getVariableDtoList(),responseEntity.getBody().getFmuPath());
            VariablePrioritizerMap.removeFmuEntryFromMap(fmuId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
    }

    @PostMapping("/changeVariableValue")
    public ResponseEntity<String> changeVariableValue(@RequestBody PrioritizeVariableEventDto event) {
        publisher.publishPrioritizeVariableEvent(event);
        return new ResponseEntity<>("succes", HttpStatus.OK);
    }

    @PostMapping("/prioritizeVariable")
    public ResponseEntity<String> prioritizeVariabel(@RequestBody PrioritizeSimulationVariableInputEventDto event) {
        VariablePrioritizerMap.prioritizeVariable(event.getFmuId(), event.getVariableName(), event.getValue());
        return new ResponseEntity<>("succes", HttpStatus.OK);
    }


    @PostMapping("/unprioritizeVariable")
    public ResponseEntity<String> unprioritizeVariable(@RequestBody PrioritizeSimulationVariableInputEventDto event) {
        VariablePrioritizerMap.unprioritizeVariable(event.getFmuId(), event.getVariableName());
        return new ResponseEntity<>("succes", HttpStatus.OK);
    }


}

