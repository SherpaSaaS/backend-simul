package org.sherpaengineering.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MappedSimulationValuesDto {
    Map<Long, Double> variableValueMap = new HashMap<>();
}
