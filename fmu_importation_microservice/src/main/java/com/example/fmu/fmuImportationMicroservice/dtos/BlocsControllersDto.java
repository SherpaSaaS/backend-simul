package com.example.fmu.fmuImportationMicroservice.dtos;

import com.example.fmu.fmuImportationMicroservice.dtos.fmu.FmuVariable;
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
public class BlocsControllersDto {
    Integer id;
    String name;
    List<FmuVariableDto> variableList;
}


