package com.example.fmu.fmuDictionaryMicroservice.services;

import com.example.fmu.fmuDictionaryMicroservice.models.FmuDictionary;
import com.example.fmu.fmuDictionaryMicroservice.repositories.DictionaryRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class FmuDictionaryService {
    @Autowired
    DictionaryRepository dictionaryRepository;



    public void addDictionaryEntry(FmuDictionary entry){
        Optional<FmuDictionary> fmuDictionaryOptional = dictionaryRepository.findByFmuXmlVariableName(entry.getFmuXmlVariableName());
        if(fmuDictionaryOptional.isEmpty()){
            dictionaryRepository.save(entry);
        }
    }

    public FmuDictionary getEntryByXmlName(String xmlVariableName){
        Optional<FmuDictionary> fmuDictionary = dictionaryRepository.findByFmuXmlVariableName(xmlVariableName);
        return fmuDictionary.orElse(null);
    }

    public List<FmuDictionary> getAllEntries(){
        return dictionaryRepository.findAll();
    }

    public Boolean deleteEntry(String xmlVariableName){
        Optional<FmuDictionary> fmuDictionary= dictionaryRepository.findByFmuXmlVariableName(xmlVariableName);
        fmuDictionary.ifPresent(dictionary -> dictionaryRepository.delete(dictionary));
        return Boolean.TRUE;
    }

    public void saveDictionaryFromExcelFile(MultipartFile excelFile){
        try (Workbook workbook = new XSSFWorkbook(excelFile.getInputStream())) {  // Replace with HSSFWorkbook if needed
            Sheet sheet = workbook.getSheetAt(0);  // Assuming first sheet

            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // Skip the first row
            String blocName = "default bloc";
            while (rowIterator.hasNext()){
                Row row = rowIterator.next();
                // Iterate through cells in the row
                FmuDictionary fmuDictionaryEntry = new FmuDictionary();

                if( row.getCell(0) == null){
                    fmuDictionaryEntry.setBlocName(blocName);
                } else if (row.getCell(0).getStringCellValue().equals("")) {
                    fmuDictionaryEntry.setBlocName(blocName);
                } else{
                    blocName = row.getCell(0).getStringCellValue();
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    int index = cell.getColumnIndex();
                    // Handle each cell's value based on its type
                    switch (index) {
                        case 0 -> {

                            fmuDictionaryEntry.setBlocName(blocName);
                        }
                        case 1 -> fmuDictionaryEntry.setFmuXmlVariableName(cell.getStringCellValue());
                        case 2 -> {
                            if (cell.getCellType() == CellType.NUMERIC) {
                                fmuDictionaryEntry.setUnit(String.valueOf(cell.getNumericCellValue()));
                            } else {
                                fmuDictionaryEntry.setUnit(cell.getStringCellValue());
                            }

                        }
                        case 3 -> fmuDictionaryEntry.setVariableNameAlias(cell.getStringCellValue());
                        default -> System.out.print("\t");
                    }
                }
                dictionaryRepository.save(fmuDictionaryEntry);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void saveDictionaryFromExcelFileIonic5(MultipartFile excelFile){
        try (Workbook workbook = new XSSFWorkbook(excelFile.getInputStream())) {  // Replace with HSSFWorkbook if needed
            Sheet sheet = workbook.getSheetAt(0);  // Assuming first sheet

            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // Skip the first row
            String blocName = "default bloc";
            while (rowIterator.hasNext()){
                Row row = rowIterator.next();
                // Iterate through cells in the row
                FmuDictionary fmuDictionaryEntry = new FmuDictionary();

                if( row.getCell(0) == null){
                    fmuDictionaryEntry.setBlocName(blocName);
                } else if (row.getCell(0).getStringCellValue().equals("")) {
                    fmuDictionaryEntry.setBlocName(blocName);
                } else{
                    blocName = row.getCell(0).getStringCellValue();
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    int index = cell.getColumnIndex();
                    // Handle each cell's value based on its type
                    switch (index) {
                        case 0 -> {

                            fmuDictionaryEntry.setBlocName(blocName);
                        }
                        case 1 -> fmuDictionaryEntry.setFmuXmlVariableName(cell.getStringCellValue());
                        case 2 -> {
                            if (cell.getCellType() == CellType.NUMERIC) {
                                fmuDictionaryEntry.setUnit(String.valueOf(cell.getNumericCellValue()));
                            } else {
                                fmuDictionaryEntry.setUnit(cell.getStringCellValue());
                            }

                        }
                        case 3 -> fmuDictionaryEntry.setVariableNameAlias(cell.getStringCellValue());
                        default -> System.out.print("\t");
                    }
                }
                dictionaryRepository.save(fmuDictionaryEntry);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
