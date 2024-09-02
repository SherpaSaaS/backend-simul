package com.example.fmu.fmuImportationMicroservice.dtos;

import com.example.fmu.fmuImportationMicroservice.models.Variable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FmuSimulationVariableDto {
    Long id;
    String name;
    boolean tunnable;
    String initValue;
    String typeName;
    boolean hasConfiguration;

    public static FmuSimulationVariableDto merge(Variable variable){
        FmuSimulationVariableDto response = new FmuSimulationVariableDto();
        response.setId(variable.getId());
        response.setName(variable.getName());
        response.setInitValue(variable.getInitValue());
        response.setTunnable(variable.isTunableVariable());
        response.setTypeName(variable.getTypeName());
        response.setHasConfiguration(variable.getVariableIHMConfiguration() != null);
        return response;
    }
}
