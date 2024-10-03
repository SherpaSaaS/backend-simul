package com.example.fmu.fmuImportationMicroservice.services;

import com.example.fmu.fmuImportationMicroservice.dtos.fmu.FmuModelDescription;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IFmuUploadService;
import com.example.fmu.fmuImportationMicroservice.utils.FileUtils;
import com.example.fmu.fmuImportationMicroservice.utils.FmuModelDescriptionReader;
import com.example.fmu.fmuImportationMicroservice.utils.PropertiesAccessor;
import jakarta.transaction.NotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
public class FmuUploadService implements IFmuUploadService {

    @Autowired
    PropertiesAccessor propertiesAccessor;


    @Override
    public String uploadFmu(MultipartFile file) throws NotSupportedException {

        Optional<String> extension = FileUtils.getExtensionByStringHandling(file.getOriginalFilename());
        if(extension.isEmpty() || !extension.get().equals("fmu")){
            throw new NotSupportedException("File not Fmu file");
        }

        /********* prepare for saving and unzipping the uploaded file ************/
        String targetFilePath = propertiesAccessor.getFmusFolder() + File.separator +   file.getOriginalFilename();
        System.out.print("targetFilePath  "+targetFilePath);
        File targetFile = new File(targetFilePath); // where the uploaded file will be saved
        System.out.print("targetFile  "+targetFile);

        String targetUnzippedDirectory = propertiesAccessor.getExtractedFmusFolder() +
                Objects.requireNonNull(file.getOriginalFilename()).replace(".fmu" , "");
        try {
            file.transferTo(targetFile.toPath()); // transfers the uploaded file data to the specified target location
            FileUtils.unzipFile(targetFilePath , targetUnzippedDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        return targetFilePath;
    }




}
