package com.example.fmu.fmuImportationMicroservice.dtos;

import com.example.fmu.fmuImportationMicroservice.models.Variable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FmuVariableDto {
    Long id;
    String name;
    String unit;
    String alias;
    boolean tunnable;
    double locationX;
    double locationY;
    int width;
    int height;


    public static FmuVariableDto merge(Variable variable){
        FmuVariableDto response = new FmuVariableDto();
        response.setTunnable(variable.isTunableVariable());
        response.setId(variable.getId());
        response.setName(variable.getName());
        response.setUnit(variable.getUnit());
        response.setAlias(variable.getAlias());
        if(variable.getVariableIHMConfiguration() != null) {
            response.setWidth(variable.getVariableIHMConfiguration().getWidth());
            response.setHeight(variable.getVariableIHMConfiguration().getHeight());
            response.setLocationX(variable.getVariableIHMConfiguration().getLocationX());
            response.setLocationY(variable.getVariableIHMConfiguration().getLocationY());
        }else{
            response.setWidth(50);
            response.setHeight(50);
            response.setLocationX(0.0);
            response.setLocationY(0.0);
        }
        return response;
    }
}
