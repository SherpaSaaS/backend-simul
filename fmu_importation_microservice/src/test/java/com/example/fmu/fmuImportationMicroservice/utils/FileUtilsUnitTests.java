package com.example.fmu.fmuImportationMicroservice.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import com.example.fmu.fmuImportationMicroservice.utils.FileUtils;
import org.junit.jupiter.api.Assertions;

public class FileUtilsUnitTests {

    @ParameterizedTest
    @CsvSource({
            "file.txt, txt",
            "document.docx, docx",
            "image.jpeg, jpeg",
            "noextension, "
            // Add more test cases as needed
    })
    public void testGetExtensionByStringHandling(String filename, String expectedExtension) {
        Optional<String> result = FileUtils.getExtensionByStringHandling(filename);

        if (expectedExtension == null) {
            Assertions.assertTrue(result.isEmpty());
        } else {
            Assertions.assertTrue(result.isPresent());
            Assertions.assertEquals(expectedExtension, result.get());
        }
    }


    @Test
    public void testUnzipFmuFile_SuccessfulExtraction() throws IOException {
        // Create a temporary zip file
        File zipFile = new File("C:\\Users\\HP\\GitHub\\FMU_prototype\\FMU_Simulator\\fmu_importation_microservice\\src\\test\\resources\\try1.fmu");

        // Call the method to test
        String destPath = System.getProperty("java.io.tmpdir") + zipFile.getName().replace(".fmu","");
        FileUtils.unzipFile(zipFile.toString() , destPath);

        // Check if the file was successfully extracted

        File extractedFile = new File(destPath);

        Assertions.assertTrue(extractedFile.exists() && extractedFile.isDirectory());

    }


    @Test
    public void testUnzipFmuFile_MissingFile() throws IOException {
        // Create a temporary zip file
        File zipFile = new File("C:\\Users\\HP\\GitHub\\FMU_prototype\\FMU_Simulator\\fmu_importation_microservice\\src\\test\\resources\\try");

        // Call the method to test
        String destPath = System.getProperty("java.io.tmpdir") + zipFile.getName().replace(".fmu","");
        Assertions.assertThrows(FileNotFoundException.class, () -> {FileUtils.unzipFile(zipFile.toString() , destPath);});

    }

    @AfterEach
    public void deleteTestFile(){
        File myObj = new File(System.getProperty("java.io.tmpdir") + "try1");
        if(myObj.exists()){
            myObj.delete();
        }
    }



}
