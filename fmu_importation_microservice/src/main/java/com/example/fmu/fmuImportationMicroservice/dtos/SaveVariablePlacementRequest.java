package com.example.fmu.fmuImportationMicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveVariablePlacementRequest {
    Long id;
    Double x;
    Double y;
}
