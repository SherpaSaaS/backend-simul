package com.example.fmu.fmuImportationMicroservice.dtos;

import com.example.fmu.fmuImportationMicroservice.dtos.fmu.FmuModelDescription;
import com.example.fmu.fmuImportationMicroservice.models.Variable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FmuUploadResponse {
    private Integer fmuId;
    private FmuModelDescription modelDescription;
    //private Integer ExtensionWind;
   // private Integer ExtensionLinux;
    private List<BlocsControllersDto> blocsControllers;
    private List<FmuVariableDto> unlinkedVariables;
    private List<FmuVariableDto> outputVariables;
    private Boolean success;
    private String message;



    public static FmuUploadResponse merge(Integer fmuId , FmuModelDescription description ,List<Variable> variables){
        FmuUploadResponse response = new FmuUploadResponse(fmuId,description , null, null ,null, true , "fmu uploaded successfully");
        //init hashmap to map bloc to list variables
        Map<BlocsControllersDto , List<FmuVariableDto>> blocVariablesMap = new HashMap<>();
        //entry for unlinked variables
        blocVariablesMap.put(null, new ArrayList<>());
        //list of output variables
        List<FmuVariableDto> outputVariables = new ArrayList<>();

        /*
            map through all variables check if tunnable(editable or not)
            if tunnable add to hashmap to their bloc if exist
            else add to output list
         */
        variables.forEach(variable -> {
            if(variable.isTunableVariable() || variable.getCausality()=="input"){
                BlocsControllersDto blocsControllersDto = variable.getInputVariableBloc() != null ? new BlocsControllersDto(
                        variable.getInputVariableBloc().getId(),
                        variable.getInputVariableBloc().getName(), null) :
                        null;
                blocVariablesMap.computeIfAbsent(blocsControllersDto, k -> new ArrayList<>());
                blocVariablesMap.get(blocsControllersDto).add(FmuVariableDto.merge(variable));
            }else{
                outputVariables.add(FmuVariableDto.merge(variable));
            }
        });

        //convert hashmap to list of BlocControllersDto
        List<BlocsControllersDto> blocsControllersDtos = new ArrayList<>();
        for(Map.Entry<BlocsControllersDto , List<FmuVariableDto>> entry : blocVariablesMap.entrySet()){
            if(entry.getKey() != null){
                entry.getKey().setVariableList(entry.getValue());
                blocsControllersDtos.add(entry.getKey());
            }
        }

        response.setUnlinkedVariables(blocVariablesMap.get(null));
        response.setOutputVariables(outputVariables);
        response.setBlocsControllers(blocsControllersDtos);

        return response;

    }
}
