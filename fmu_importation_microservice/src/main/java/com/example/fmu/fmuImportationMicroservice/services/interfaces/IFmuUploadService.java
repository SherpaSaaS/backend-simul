package com.example.fmu.fmuImportationMicroservice.services.interfaces;

import jakarta.transaction.NotSupportedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


public interface IFmuUploadService {
    public String uploadFmu(MultipartFile file) throws NotSupportedException;

}
