package com.example.fmu.fmuImportationMicroservice.dtos.fmu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FmuDefaultExperiment {
    private double startTime;
    private double stopTime;
    private double stepSize;
}
