package com.example.fmu.fmuImportationMicroservice.dtos.fmu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "fmiModelDescription")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FmuModelDescription {

    @JacksonXmlProperty(isAttribute = true)
    private String fmiVersion;

    @JacksonXmlProperty(isAttribute = true)
    private String modelName;

    @JacksonXmlProperty(isAttribute = true)
    private String guid;

    @JacksonXmlProperty(isAttribute = true)
    private String description;

    @JacksonXmlProperty(isAttribute = true)
    private String generationTool;

    @JacksonXmlProperty(isAttribute = true)
    private String version;

    @JacksonXmlProperty(isAttribute = true)
    private String variableNamingConvention;

    @JacksonXmlProperty(isAttribute = true)
    private String generationDateAndTime;

    @JacksonXmlProperty(isAttribute = true)
    private int numberOfEventIndicators;

    @JacksonXmlElementWrapper(localName = "CoSimulation")
    private FmuCoSimulation coSimulation;

    @JacksonXmlElementWrapper(localName = "DefaultExperiment")
    private FmuDefaultExperiment defaultExperiment;


    @JacksonXmlElementWrapper(localName = "ModelVariables")
    private List<FmuVariable> modelVariablesList;


}
