package com.example.fmu.fmuImportationMicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadFmuImageResponse {
    Integer fmuId;
    Boolean success;
}
