package com.example.fmuTest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnPrioritizeSimulationVariableInputEventDto {
    Integer fmuId;
    String variableName;
    String typeName;
}
