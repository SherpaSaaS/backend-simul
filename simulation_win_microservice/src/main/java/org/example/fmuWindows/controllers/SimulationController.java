package org.example.fmuWindows.controllers;

import org.example.fmuWindows.models.FileValidationResult;
import jakarta.servlet.http.HttpServletRequest;
import org.example.fmuWindows.dtos.GetFmuSimulationInfoResponse;
import org.example.fmuWindows.dtos.PrioritizeSimulationVariableInputEventDto;
import org.example.fmuWindows.dtos.PrioritizeVariableEventDto;
import org.example.fmuWindows.eventPublishers.PrioritizeVariableEventPublisher;
import org.example.fmuWindows.models.VariablePrioritizerMap;
import org.example.fmuWindows.models.VariablesValues;
import org.example.fmuWindows.services.CsvScenarioService;
import org.example.fmuWindows.services.DataToCSVService;
import org.example.fmuWindows.services.FmuService;
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
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/api/simulationWin")
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
    public ResponseEntity<Boolean> testSimulation(HttpServletRequest request , @PathVariable("id") Integer fmuId) {
        try {
            //to call the variableController of the importation microService
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
            fmu.runDefaultSimulation(responseEntity.getBody().getFmuId(), responseEntity.getBody().getVariableDtoList());
            VariablePrioritizerMap.removeFmuEntryFromMap(fmuId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
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

