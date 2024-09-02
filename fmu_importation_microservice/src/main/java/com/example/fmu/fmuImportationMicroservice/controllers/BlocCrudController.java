package com.example.fmu.fmuImportationMicroservice.controllers;


import com.example.fmu.fmuImportationMicroservice.dtos.BlocsControllersDto;
import com.example.fmu.fmuImportationMicroservice.dtos.CreateNewBlockRequestBody;
import com.example.fmu.fmuImportationMicroservice.dtos.FmuVariableDto;
import com.example.fmu.fmuImportationMicroservice.models.Fmu;
import com.example.fmu.fmuImportationMicroservice.models.InputVariableBloc;
import com.example.fmu.fmuImportationMicroservice.models.Variable;
import com.example.fmu.fmuImportationMicroservice.repositories.InputBlocRepository;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IFmuService;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/bloc")

@EnableDiscoveryClient
public class BlocCrudController {

    @Autowired
    IFmuService fmuService;

    @Autowired
    IVariableService variableService;

    @Autowired
    InputBlocRepository inputBlocRepository;

    @PostMapping("/create")
    public ResponseEntity<BlocsControllersDto> createNewControllerBloc(@RequestBody CreateNewBlockRequestBody requestBody){
        Fmu fmu = fmuService.getFmuById(requestBody.getFmuId());

        List<InputVariableBloc> blocs = fmu.getInputVariableBlocs();
        InputVariableBloc newBloc = new InputVariableBloc(null,requestBody.getNewBlocName(),List.of(fmu),null);
        blocs.add(newBloc);
        fmu.setInputVariableBlocs(blocs);

        fmuService.save(fmu);
        List<Variable> selectedVariabels = fmu.getVariableList().stream().filter(variable -> requestBody.getSelectedVariablesIds().contains(variable.getId())).toList();

        selectedVariabels.forEach(variable -> {
            if(requestBody.getSelectedVariablesIds().contains(variable.getId())){
                if(variable.getInputVariableBloc() != null){
                    throw new RuntimeException("vairable already in bloc");
                }else{
                    variable.setInputVariableBloc(newBloc);
                }
            }
        });

        variableService.saveAll(selectedVariabels);

        BlocsControllersDto response = new BlocsControllersDto();
        response.setId(newBloc.getId());
        response.setName(newBloc.getName());
        response.setVariableList(selectedVariabels.stream().map(FmuVariableDto::merge).toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{fmuId}/{id}")
    public ResponseEntity<Boolean> deleteBlocController(@PathVariable("id") Integer blocId , @PathVariable("fmuId") Integer fmuId){
        InputVariableBloc bloc = inputBlocRepository.findById(blocId).get();
        variableService.saveAll(
                bloc.getVariableList().stream().map(variable -> {
                    variable.setInputVariableBloc(null);
                    return variable;
                }).collect(Collectors.toList())
        );
        Fmu fmu = fmuService.getFmuById(fmuId);
        fmu.getInputVariableBlocs().remove(bloc);
        fmuService.save(fmu);
        if(bloc.getFmu().size()==1){
            inputBlocRepository.delete(bloc);
        }
        return new ResponseEntity<>(Boolean.TRUE,HttpStatus.OK);
    }
}
