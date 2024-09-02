package com.example.fmu.fmuImportationMicroservice.dtos.fmu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//TODO make it main FMU file
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fmu {
    private FmuModelDescription modelDescription;
    private String modelDescriptionFilePath;
    private String libraryFilePath;
}
