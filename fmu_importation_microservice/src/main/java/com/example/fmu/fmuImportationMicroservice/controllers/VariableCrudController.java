package com.example.fmu.fmuImportationMicroservice.controllers;


import com.example.fmu.fmuImportationMicroservice.dtos.SaveVariablePlacementRequest;
import com.example.fmu.fmuImportationMicroservice.models.Variable;
import com.example.fmu.fmuImportationMicroservice.models.VariableIHMConfiguration;
import com.example.fmu.fmuImportationMicroservice.repositories.InputBlocRepository;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IFmuService;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class VariableCrudController {

    @Autowired
    IVariableService variableService;

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
