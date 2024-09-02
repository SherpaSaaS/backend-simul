package com.example.fmu.fmuImportationMicroservice.dtos;

import com.example.fmu.fmuImportationMicroservice.models.Fmu;
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
public class ConfigurationInterfaceResponse {
    private Integer fmuId;
    private List<BlocsControllersDto> blocsControllers;
    private List<FmuVariableDto> unlinkedVariables;
    private List<FmuVariableDto> outputsVariables;
    private String imagePath;
    private Boolean success;
    private String message;

    public static ConfigurationInterfaceResponse merge(Fmu fmu){
        ConfigurationInterfaceResponse response = new ConfigurationInterfaceResponse();
        //init hashmap to map bloc to list variables
        Map<BlocsControllersDto , List<FmuVariableDto>> blocVariablesMap = new HashMap<>();
        //entry for unlinked variables
        blocVariablesMap.put(null, new ArrayList<>());
        //add all blocs
        fmu.getInputVariableBlocs().forEach(bloc -> {
            blocVariablesMap.put(new BlocsControllersDto(bloc.getId(), bloc.getName(), null)  , new ArrayList<>());
        });
        //list of output variables
        List<FmuVariableDto> outputVariables = new ArrayList<>();

        /*
            map through all variables check if tunnable(editable or not)
            if not tunnable and their ihm config is not null place them in output list
         */
        fmu.getVariableList().forEach(variable -> {
            if(variable.isTunableVariable() || variable.getVariableIHMConfiguration()==null){
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
        response.setOutputsVariables(outputVariables);
        response.setBlocsControllers(blocsControllersDtos);
        response.setFmuId(fmu.getId());
        response.setImagePath(fmu.getModelImagePath());
        response.setMessage("success");
        response.setSuccess(Boolean.TRUE);

        return response;

    }
}
