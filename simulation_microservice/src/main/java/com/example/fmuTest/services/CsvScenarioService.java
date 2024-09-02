package com.example.fmuTest.services;


import com.example.fmuTest.dtos.FmuSimulationVariableDto;
import com.example.fmuTest.dtos.GetFmuSimulationInfoResponse;
import com.example.fmuTest.models.FileValidationResult;
import com.example.fmuTest.models.VariablesValues;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.lang3.tuple.Pair;
import org.javafmi.wrapper.v1.Access;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvScenarioService {
    public ResponseEntity<FileValidationResult> checkFile(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new FileValidationResult(false, "Please select a file to upload"));

        }
        // Basic validation - check file extension
        Path baseDir = Paths.get("C:/Users/Latitude_3520/Desktop/Stage PFE/csvFiles"); // Replace with your actual base directory
        Path filePath = baseDir.resolve(file.getOriginalFilename());
        String fileName = filePath.toString();
        if (!fileName.toLowerCase().endsWith(".csv")) {
            return ResponseEntity.unprocessableEntity().body(new FileValidationResult(false, "Only CSV files are allowed"));
        }
        try {
            if (!Files.exists(Paths.get(fileName))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new FileValidationResult(false, "File not found: " + fileName));
            }

                return ResponseEntity.ok(new FileValidationResult(true, filePath.toString()));
            } catch (Exception e) { // Catch-all for unexpected exceptions
                return ResponseEntity.internalServerError().body(new FileValidationResult(false, "Error processing CSV file: Unexpected error. Details: " + e.getMessage()));
            }
}
    public List<String> getInputVariableList(long fmuId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GetFmuSimulationInfoResponse> responseEntity = restTemplate.getForEntity("http://localhost:8080/api/getFmuVariables/"+fmuId, GetFmuSimulationInfoResponse.class);

        List<FmuSimulationVariableDto> variableList = responseEntity.getBody().getVariableDtoList();
        List<String> inputVariableList = new ArrayList<>(); // Use an ArrayList for dynamic size

        for (FmuSimulationVariableDto variable : variableList) {
            if (variable.isTunnable()) {
                inputVariableList.add(variable.getName()); // Add the name as a String to the list
            }
        }
        return inputVariableList;
    }

    public  List<String> filterInputs(String[] variableNames, List<String> inputVariableList) {
        // Convert  string to list of string for searching the input variables in the csv
        List<String> variableNamesList = Arrays.asList(variableNames);
        List<String> newVariableList;
        newVariableList=variableNamesList.stream()
                .filter(variable -> inputVariableList.contains(variable))
                .collect(Collectors.toList());


        return newVariableList;

    }

    public String extractFinalValue(List<String[]> values,long fmuId) {
        Access access = null;
         //test if the final default value  or scenario value is bigger
        String finalScenariValue=values.stream()
                .filter(line -> line != null) // Filter out null lines
                .map(line -> line[0]) // Map each line to the first value
                .reduce((first, second) -> second) // Get the last value using reduce
                .orElse(null); // Handle empty file or missing column
        Double finalDefaultValue =access.getModelDescription().getDefaultExperiment().getStopTime();
     if  (Double.valueOf(finalScenariValue)>finalDefaultValue)
         return finalScenariValue;
     else
         return finalDefaultValue.toString();

    }

    public VariablesValues getReadData(String filePath) {
        Reader reader = null;
        String[] variableNames;
        List<String[]> values;
        try {
            reader = Files.newBufferedReader(Path.of(filePath));
            // create csv reader
            CSVReader csvReader = new CSVReader(reader);
            // extract columns : Variable names
            String[] variableColumns = csvReader.readNext();
            // Skip the first column (if it exists)
            variableNames = variableColumns != null ? Arrays.copyOfRange(variableColumns, 1, variableColumns.length) : null;
            //get the units column
            csvReader.readNext();
            // read all the values of my variables in the csv
            values = csvReader.readAll();
            csvReader.close();
            reader.close();
            return new VariablesValues(variableNames, values);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }



    }
}
