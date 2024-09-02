package com.example.fmu.fmuImportationMicroservice.dtos.fmu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FmuVariable {
    private String name;
    private String causality;
    private String variability;
    private String description;
    private String typeName;
    private String initValue;
    @JacksonXmlElementWrapper(localName = "Real")
    private RealType real;
    @JacksonXmlElementWrapper(localName = "String")
    private TypeData string;
    @JacksonXmlElementWrapper(localName = "Enumeration")
    private TypeData enumerate;
    @JacksonXmlElementWrapper(localName = "Boolean")
    private TypeData bool;
    @JacksonXmlElementWrapper(localName = "Integer" )
    private TypeData integer;

    public String getInitValue(){
        if(real != null)
            initValue = real.getStart().toString();
        if(integer != null)
            initValue = integer.getStart();
        if(enumerate != null)
            initValue = enumerate.getStart();
        if(bool != null)
            initValue = bool.getStart();
        if(string != null)
            initValue = string.getStart();

        return initValue != null ? initValue : "0";
    }

    public String getTypeName(){
        if(real != null)
            typeName = "real";
        if(integer != null)
            typeName = "integer";
        if(enumerate != null)
            typeName ="enumerate";
        if(bool != null)
            typeName = "boolean";
        if(string != null)
            typeName = "string";

        return typeName;
    }
}
