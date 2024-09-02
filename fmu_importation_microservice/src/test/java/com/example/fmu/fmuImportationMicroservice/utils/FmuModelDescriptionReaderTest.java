package com.example.fmu.fmuImportationMicroservice.utils;

import com.example.fmu.fmuImportationMicroservice.dtos.fmu.FmuModelDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

public class FmuModelDescriptionReaderTest {



    @Test
    public void testJacksonXmlReadStringFmuDescription(){
        String xmlString = "<fmiModelDescription fmiVersion=\"2.0\" modelName=\"try1\" guid=\"{fcc30679-5920-6414-b10c-701dc631afcc}\" description=\"\" generationTool=\"Simulink (R2021a)\" version=\"1.1\" variableNamingConvention=\"structured\" generationDateAndTime=\"2024-01-31T15:53:43Z\" numberOfEventIndicators=\"0\">\n" +
                "<CoSimulation modelIdentifier=\"try1\" canBeInstantiatedOnlyOncePerProcess=\"true\" canNotUseMemoryManagementFunctions=\"true\">\n" +
                "<SourceFiles>\n" +
                "<File name=\"try1.c\"/>\n" +
                "<File name=\"try1_data.c\"/>\n" +
                "<File name=\"try1_fmu.c\"/>\n" +
                "<File name=\"lccstub.c\"/>\n" +
                "</SourceFiles>\n" +
                "</CoSimulation>\n" +
                "<DefaultExperiment startTime=\"0\" stopTime=\"10\" stepSize=\"0.1\"/>\n" +
                "<VendorAnnotations>\n" +
                "<Tool name=\"Simulink\">\n" +
                "<Simulink>\n" +
                "<ImportCompatibility requireRelease=\"all\" requireMATLABOnPath=\"no\"/>\n" +
                "<SimulinkModelInterface>\n" +
                "<Inport dataType=\"double\" portNumber=\"1\" portName=\"In1\"/>\n" +
                "<Outport dataType=\"double\" portNumber=\"1\" portName=\"Out1\"/>\n" +
                "</SimulinkModelInterface>\n" +
                "</Simulink>\n" +
                "</Tool>\n" +
                "</VendorAnnotations>\n" +
                "<ModelVariables>\n" +
                "<ScalarVariable name=\"In1\" valueReference=\"0\" description=\"In1\" causality=\"input\" variability=\"continuous\">\n" +
                "<Real start=\"0\"/>\n" +
                "</ScalarVariable>\n" +
                "<!--  index=\"1\"  -->\n" +
                "<ScalarVariable name=\"Out1\" valueReference=\"1\" description=\"Out1\" causality=\"output\" variability=\"continuous\" initial=\"calculated\">\n" +
                "<Real/>\n" +
                "</ScalarVariable>\n" +
                "<!--  index=\"2\"  -->\n" +
                "<ScalarVariable name=\"time\" valueReference=\"2\" description=\"time\" causality=\"independent\" variability=\"continuous\">\n" +
                "<Real/>\n" +
                "</ScalarVariable>\n" +
                "<!--  index=\"3\"  -->\n" +
                "</ModelVariables>\n" +
                "<ModelStructure>\n" +
                "<Outputs>\n" +
                "<Unknown index=\"2\"/>\n" +
                "</Outputs>\n" +
                "<InitialUnknowns>\n" +
                "<Unknown index=\"2\"/>\n" +
                "</InitialUnknowns>\n" +
                "</ModelStructure>\n" +
                "</fmiModelDescription>";

        FmuModelDescription description = FmuModelDescriptionReader.getModelDescriptionFromFmuDescriptionXmlString(xmlString);
        Assertions.assertTrue(
                description!=null &&
                description.getModelVariablesList().size() == 3 &&
                description.getCoSimulation() != null &&
                description.getModelName() != null && description.getModelName().equals("try1")
                );
    }



    @ParameterizedTest
    @ValueSource(strings = "C:\\Users\\HP\\GitHub\\FMU_prototype\\FMU_Simulator\\fmu_importation_microservice\\src\\test\\resources\\modelDescription.xml")
    public void readFmuDescriptionFromXmlFile(String descriptionFilePath){
        FmuModelDescription description = FmuModelDescriptionReader.getModelDescriptionFromFmuDescriptionXmlFile(new File(descriptionFilePath));
        Assertions.assertTrue(
                description!=null &&
                        description.getModelVariablesList().size() == 3 &&
                        description.getCoSimulation() != null &&
                        description.getModelName() != null && description.getModelName().equals("try1")
        );
    }



}
