package com.example.fmu.fmuImportationMicroservice.controllers;


import com.example.fmu.fmuImportationMicroservice.dtos.FmuSimulationVariableDto;
import com.example.fmu.fmuImportationMicroservice.dtos.FmuVariableDto;
import com.example.fmu.fmuImportationMicroservice.dtos.GetFmuVariablesResponse;
import com.example.fmu.fmuImportationMicroservice.dtos.SaveVariablePlacementRequest;
import com.example.fmu.fmuImportationMicroservice.models.Fmu;
import com.example.fmu.fmuImportationMicroservice.models.Variable;
import com.example.fmu.fmuImportationMicroservice.models.VariableIHMConfiguration;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IFmuService;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/variable")
public class VariableController {
    @Autowired
    IFmuService fmuService;

    @Autowired
    IVariableService variableService;

    @GetMapping("/getVariable/{id}")
    public ResponseEntity<GetFmuVariablesResponse> getFmuVariableList(@PathVariable("id")Integer fmuId){
        Fmu fmu = fmuService.getFmuById(fmuId);
        List<FmuSimulationVariableDto> variableDtoList = fmu.getVariableList()
                .stream()
                .map(FmuSimulationVariableDto::merge).toList();
        GetFmuVariablesResponse response = new GetFmuVariablesResponse(fmuId,variableDtoList,fmu.getFilePath());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/makeVariablePlaceableOnModel/{id}")
    public ResponseEntity<Boolean> makeVariablePlaceableOnModel(@PathVariable("id") Long id){
        Variable variable = variableService.getVariableById(id);
        variable.setVariableIHMConfiguration(new VariableIHMConfiguration());
        variableService.save(variable);
        return new ResponseEntity<>(Boolean.TRUE , HttpStatus.OK);
    }

    @PostMapping("/saveVariablePlacementOnModel/")
    public ResponseEntity<Boolean> saveVariablePlacementOnModel(@RequestBody SaveVariablePlacementRequest saveVariablePlacementRequest){
        Variable variable = variableService.getVariableById(saveVariablePlacementRequest.getId());
        VariableIHMConfiguration configuration = variable.getVariableIHMConfiguration();
        configuration.setLocationX(saveVariablePlacementRequest.getX());
        configuration.setLocationY(saveVariablePlacementRequest.getY());
        variable.setVariableIHMConfiguration(configuration);
        variableService.save(variable);
        return new ResponseEntity<>(Boolean.TRUE , HttpStatus.OK);
    }
}
