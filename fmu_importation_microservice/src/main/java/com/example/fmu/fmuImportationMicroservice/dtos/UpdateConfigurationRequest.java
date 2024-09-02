package com.example.fmu.fmuImportationMicroservice.dtos;

import java.util.List;

public class UpdateConfigurationRequest {
    private Integer fmuId;
    private List<BlocsControllersDto> blocsControllers;
    private List<FmuVariableDto> unlinkedVariables;
    private List<FmuVariableDto> outputs;
}
