package com.example.fmu.fmuImportationMicroservice.utils;

import com.example.fmu.fmuImportationMicroservice.dtos.fmu.FmuModelDescription;
import com.example.fmu.fmuImportationMicroservice.models.Fmu;
import com.example.fmu.fmuImportationMicroservice.models.InputVariableBloc;
import com.example.fmu.fmuImportationMicroservice.models.Variable;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IVariableService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FmuHelper {

    public static List<Variable> getVariableList(FmuModelDescription description, Fmu finalFmu, IVariableService variableService){
        return description.getModelVariablesList()
                .stream()
                .map(var -> {
                    //TODO communicate with dictionary micro service to get variable alias and bloc etc ... instead of only BlocInput
                    InputVariableBloc inputVariableBlocFinal = variableService.getVariableBlocFromDictionary(finalFmu , var);
                    return Variable.builder()
                            .causality(var.getCausality())
                            .variability(var.getVariability())
                            .description(var.getDescription())
                            .name(var.getName())
                            .typeName(var.getTypeName())
                            .initValue(var.getInitValue())
                            .fmu(finalFmu)
                            .inputVariableBloc(inputVariableBlocFinal)
                            .build();
                })
                .toList();

    }
}
