package com.example.fmu.fmuImportationMicroservice.dtos.fmu;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FmuList {
    private String name;
    private String description;
    private  String  modelImagePath;
    private Boolean success;
    private String error;
}
