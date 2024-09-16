package org.example.fmuWindows.dtos;

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
}
