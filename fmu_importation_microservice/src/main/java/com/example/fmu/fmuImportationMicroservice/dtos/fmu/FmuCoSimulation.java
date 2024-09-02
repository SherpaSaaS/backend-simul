package com.example.fmu.fmuImportationMicroservice.dtos.fmu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FmuCoSimulation {

    @JacksonXmlProperty(isAttribute = true)
    private String modelIdentifier;

    @JacksonXmlProperty(isAttribute = true)
    private boolean canBeInstantiatedOnlyOncePerProcess;

    @JacksonXmlProperty(isAttribute = true)
    private boolean canNotUseMemoryManagementFunctions;

}