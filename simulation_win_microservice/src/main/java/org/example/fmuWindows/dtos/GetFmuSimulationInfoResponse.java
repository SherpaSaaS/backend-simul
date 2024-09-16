package org.example.fmuWindows.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFmuSimulationInfoResponse {
    private Integer fmuId;
    private List<FmuSimulationVariableDto> variableDtoList;
    private String fmuPath;
}
