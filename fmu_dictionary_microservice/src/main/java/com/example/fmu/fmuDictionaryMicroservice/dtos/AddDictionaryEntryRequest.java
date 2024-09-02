package com.example.fmu.fmuDictionaryMicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddDictionaryEntryRequest {
    String blocName;
    String xmlFmuVariableName;
    String unit;
    String variableAlias;
}
