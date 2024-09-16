package org.example.fmuWindows.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrioritizeVariableEventDto {
    private String variableName;
    private Boolean prior;
    private Object value;
    private Integer variableReference;
    private String fmuName;

}
