package com.example.fmu.fmuImportationMicroservice.utils;

import com.example.fmu.fmuImportationMicroservice.dtos.fmu.FmuModelDescription;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;



import java.io.File;


public class FmuModelDescriptionReader {


    public static FmuModelDescription getModelDescriptionFromFmuDescriptionXmlFile(File modelDescriptionFile){
        XmlMapper mapper = new XmlMapper();
        try{
            return mapper.readValue(modelDescriptionFile , FmuModelDescription.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static FmuModelDescription getModelDescriptionFromFmuDescriptionXmlString(String modelDescriptionXmlString){
        XmlMapper mapper = new XmlMapper();
        try{
            return mapper.readValue(modelDescriptionXmlString , FmuModelDescription.class);
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }


}
