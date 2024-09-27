package org.example.fmuWindows.services;

import com.opencsv.ICSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.example.fmuWindows.models.Input;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class DataToCSVService {
    public List<Input> getData(MultipartFile file){
        List<Input> inputs = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            // Iterate over rows and cells
            int rowIndex = 0;
            for (Row row : sheet) {
                if(rowIndex == 0){
                    rowIndex++;
                    continue;
                }

                Iterator<Cell> cellIterator = row.iterator();
                Input input = new Input();

                int cellIndex = 0;
                while(cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    switch (cellIndex){
                        case 0 -> input.setTime(cell.getNumericCellValue());
                        case 1 -> input.setValue(cell.getNumericCellValue());
                        default -> {}
                    }
                    input.setName(sheet.getRow(0).getCell(cellIndex).getStringCellValue());
                    cellIndex++;
                }
                inputs.add(input);
            }
            return inputs;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeValuesInCSV(ICSVWriter writer , double[] values , int i){
        if (values.length > 1) {
            String[] rowData = new String[values.length+1 ];
            rowData[0]=String.valueOf(i).concat("/").concat(String.valueOf(LocalTime.now()));

            for (int j = 1; j <rowData.length; j++) {
                // prepare the array for writting a row
                rowData[j] = String.valueOf(values[j-1]);
            }
            // for each column : write the array of values
            writer.writeAll(Collections.singleton(rowData));
        } else {
            // Handle empty values case (optional)
            // You could log a message or skip writing the row
            System.out.println("No data available for step " + i);
        }

    }
}
