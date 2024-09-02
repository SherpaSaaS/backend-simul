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
public class RealType {

    @JacksonXmlProperty(isAttribute = true , localName = "start")
    private Double start;


    public Double getStart(){
        if(start == null){
            return 0.0;
        }
        return start;
    }


}
