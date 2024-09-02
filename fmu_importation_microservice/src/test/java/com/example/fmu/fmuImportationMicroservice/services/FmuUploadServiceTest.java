package com.example.fmu.fmuImportationMicroservice.services;

import com.example.fmu.fmuImportationMicroservice.utils.PropertiesAccessor;
import jakarta.transaction.NotSupportedException;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Pattern;

@ExtendWith(MockitoExtension.class)
public class FmuUploadServiceTest {
    @Mock
    PropertiesAccessor propertiesAccessor;

    @InjectMocks
    FmuUploadService fmuUploadService;

    private static MultipartFile fmu;

    @BeforeAll
    public static void init() throws IOException {
        File file = new File("C:\\Users\\HP\\GitHub\\FMU_prototype\\FMU_Simulator\\fmu_importation_microservice\\src\\test\\resources\\try1.fmu");
        FileInputStream fileInputStream = new FileInputStream(file);
        fmu = new MockMultipartFile("file", file.getName(), null, fileInputStream);
    }

    @AfterEach
    public void deleteFile(){
        File file = new File(PropertiesAccessor.FMUS_FOLDER_DEFAULT + "try1.fmu");
        File extractedFile = new File(PropertiesAccessor.FMUS_FOLDER_UNZIPPED_DEFAULT + "try1");
        if(file.exists())
            file.delete();
        if(extractedFile.exists())
            extractedFile.delete();
    }

    @Test
    public void testUploadFileServiceCheckFileUploaded() throws NotSupportedException {
        Mockito.when(propertiesAccessor.getFmusFolder()).thenReturn(PropertiesAccessor.FMUS_FOLDER_DEFAULT);
        Mockito.when(propertiesAccessor.getExtractedFmusFolder()).thenReturn(PropertiesAccessor.FMUS_FOLDER_UNZIPPED_DEFAULT);
        File uploadedFile = new File(fmuUploadService.uploadFmu(fmu));
        Assertions.assertTrue(uploadedFile.exists());
    }

    @Test
    public void testUploadFileServiceCheckFileExtracted() throws NotSupportedException{
        Mockito.when(propertiesAccessor.getFmusFolder()).thenReturn(PropertiesAccessor.FMUS_FOLDER_DEFAULT);
        Mockito.when(propertiesAccessor.getExtractedFmusFolder()).thenReturn(PropertiesAccessor.FMUS_FOLDER_UNZIPPED_DEFAULT);
        String[] splits = fmuUploadService.uploadFmu(fmu).split(Pattern.quote(File.separator));
        String extractedFolderName = splits[splits.length - 1].replace(".fmu","");
        File extractedFile = new File(
                PropertiesAccessor.FMUS_FOLDER_UNZIPPED_DEFAULT +
                extractedFolderName
                );
        Assertions.assertTrue(extractedFile.exists());
    }

    @Test
    public void testUploadFileServiceNoFmuFile() throws NotSupportedException{
        File file = new File("C:\\Users\\HP\\GitHub\\FMU_prototype\\FMU_Simulator\\fmu_importation_microservice\\src\\test\\resources\\test.txt");
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            MultipartFile testNotFmuMultipartFile = new MockMultipartFile("file", file.getName(), null, fileInputStream);
            Assertions.assertThrowsExactly(NotSupportedException.class, () -> fmuUploadService.uploadFmu(testNotFmuMultipartFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @After("testUploadFileServiceNoFmuFile")
    public void deleteTestNotFmuFile(){
        File testFile = new File(PropertiesAccessor.FMUS_FOLDER_DEFAULT + "test.txt");
        File extracted = new File(PropertiesAccessor.FMUS_FOLDER_UNZIPPED_DEFAULT + "test.txt");
        if(testFile.exists())
            testFile.delete();
        if(extracted.exists())
            extracted.delete();
    }


}
