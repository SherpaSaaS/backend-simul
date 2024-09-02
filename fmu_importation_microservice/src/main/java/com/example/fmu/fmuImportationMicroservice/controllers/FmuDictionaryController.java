package com.example.fmu.fmuImportationMicroservice.controllers;

import com.example.fmu.fmuImportationMicroservice.dtos.AddDictionaryEntryRequest;
import com.example.fmu.fmuImportationMicroservice.models.FmuDictionary;
import com.example.fmu.fmuImportationMicroservice.services.FmuDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/api")
public class FmuDictionaryController {
    @Autowired
    private FmuDictionaryService fmuDictionaryService;

    @PostMapping("/insertDictionary")
    public ResponseEntity<Boolean> addDictionaryFromExcel(@RequestParam("file")MultipartFile excelFile){
        fmuDictionaryService.saveDictionaryFromExcelFile(excelFile);
        return new ResponseEntity<>(Boolean.TRUE , HttpStatus.OK);
    }

    @PostMapping("/insertDictionaryEntry")
    public ResponseEntity<Boolean> addDictionaryEntry(@RequestBody AddDictionaryEntryRequest requestBody){
        FmuDictionary entry = new FmuDictionary();
        entry.setUnit(requestBody.getUnit());
        entry.setFmuXmlVariableName(requestBody.getXmlFmuVariableName());
        entry.setBlocName(requestBody.getBlocName());
        entry.setVariableNameAlias(requestBody.getVariableAlias());
        fmuDictionaryService.addDictionaryEntry(entry);
        return new ResponseEntity<>(Boolean.TRUE , HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<FmuDictionary>> getAllEntries(){
        List<FmuDictionary> list = fmuDictionaryService.getAllEntries();
        return new ResponseEntity<>(list , HttpStatus.OK);
    }
}
