package org.sherpaengineering.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimulationValuesDto {
    List<Long> ids = new ArrayList<>();
    List<String> variableNames = new ArrayList<>();
    double time;
    double[] values;
}
