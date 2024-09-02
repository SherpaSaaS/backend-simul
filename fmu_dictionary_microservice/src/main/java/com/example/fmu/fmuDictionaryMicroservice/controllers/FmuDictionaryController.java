package com.example.fmu.fmuDictionaryMicroservice.controllers;

import com.example.fmu.fmuDictionaryMicroservice.dtos.AddDictionaryEntryRequest;
import com.example.fmu.fmuDictionaryMicroservice.models.FmuDictionary;
import com.example.fmu.fmuDictionaryMicroservice.services.FmuDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@CrossOrigin("*")
@RequestMapping("/api/dictionary")
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

    @GetMapping("/get/{name}")
    public ResponseEntity<FmuDictionary> getEntry(@Param("name") String variableXmlName){
        FmuDictionary fmuDictionary = fmuDictionaryService.getEntryByXmlName(variableXmlName);
        if(fmuDictionary == null){
            return new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(fmuDictionary , HttpStatus.OK);
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<Boolean> deleteEntry(@Param("name") String variableXmlName){
        fmuDictionaryService.deleteEntry(variableXmlName);
        return new ResponseEntity<>(Boolean.TRUE , HttpStatus.OK);
    }
}
