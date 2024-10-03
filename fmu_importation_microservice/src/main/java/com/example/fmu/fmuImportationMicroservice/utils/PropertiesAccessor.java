package com.example.fmu.fmuImportationMicroservice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PropertiesAccessor {

    @Autowired
    private Environment environment;


    public static final String FMUS_FOLDER_ENV_VARIABLE = "FMU_UPLOAD_FOLDER";

    public static final String FMUS_FOLDER_PROPERTY_VARIABLE = "fmu.upload.folder";
    public static final String FMUS_FOLDER_DEFAULT = System.getProperty("java.io.tmpdir");


    public static final String FMUS_FOLDER_UNZIPPED_ENV_VARIABLE = "FMU_UPLOAD_FOLDER_UNZIPPED";
    public static final String FMUS_FOLDER_UNZIPPED_PROPERTY_VARIABLE = "fmu.upload.folder.unzipped";
    public static final String FMUS_FOLDER_UNZIPPED_DEFAULT = System.getProperty("java.io.tmpdir") + "unzippedFmus" + File.separator;

    @Value("${fmu.model.image.folder:C:\\Users\\HP\\GitHub\\FMU_prototype\\ReactProject\\front-end\\public\\modelImage\\}")
    public String FMU_MODEL_IMAGE_FOLDER;

    public String ev(String env_key, String key, String default_value) {
        String value = System.getenv(env_key);
        if (value == null) {
            return environment.getProperty(key,default_value);
        }
        return value;
    }

    public String getFmusFolder(){
        return ev(
                FMUS_FOLDER_ENV_VARIABLE,
                FMUS_FOLDER_PROPERTY_VARIABLE,
                FMUS_FOLDER_DEFAULT
        );
    }

    public String getExtractedFmusFolder(){
        return ev(FMUS_FOLDER_UNZIPPED_ENV_VARIABLE,
                FMUS_FOLDER_UNZIPPED_PROPERTY_VARIABLE,
                FMUS_FOLDER_UNZIPPED_DEFAULT);
    }

    public String getFmuImageFolder (){
        return FMU_MODEL_IMAGE_FOLDER;
    }
}
