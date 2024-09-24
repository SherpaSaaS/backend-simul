package com.example.fmuTest.services;

import com.example.fmuTest.dtos.FmuSimulationVariableDto;
import com.example.fmuTest.dtos.SimulationValuesDto;
import com.example.fmuTest.eventListeners.PrioritizeVariableEventListner;
import com.example.fmuTest.models.Input;
import com.example.fmuTest.models.Output;
import com.example.fmuTest.models.Variable;
import com.example.fmuTest.models.VariablePrioritizerMap;
import com.example.fmuTest.utils.DoubleComparator;
import com.example.fmuTest.utils.FmuVariableExtractor;
import com.example.fmuTest.utils.VariableAccessor;
import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import org.javafmi.wrapper.Simulation;

import org.javafmi.wrapper.v2.Access;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class FmuService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Autowired
    PrioritizeVariableEventListner prioritizeVariableEventListner;

    @Autowired
    DataToCSVService dataToCSVService;

    @Value("${fmu.upload.folder:C:\\Users\\HP\\Desktop\\fmutestupload\\}")
    private String fmusFolder;

    public ICSVWriter exportToCsv(List<String> variableNames) {
            /*
         Define the  filename with the current time
         */
        String baseFileName = "dataExport_";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentTime = dateFormat.format(new Date());
        // Combine filename and timestamp
        String fileName = baseFileName + currentTime + ".csv";
     /*
        write the header (columns ) of the csv from the variable names array + step
     */
        String[] columns = Stream.concat(
                Stream.of("step/time"),
                variableNames.stream()
        ).toArray(String[]::new);
       /*
         Csv init part
     */
        ICSVWriter csvWriter;
        try {
            Writer writer = Files.newBufferedWriter(Paths.get(fileName));
            csvWriter = new CSVWriterBuilder(writer)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            csvWriter.writeNext(columns);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return csvWriter;
    }


    public void writePriorVariable(Integer fmuId, Simulation simulation) {
        if (!VariablePrioritizerMap.isPriorVariablesEmpty(fmuId)) {
            // lists of prioritized variables and their values to be converted t oarray and use write function in javafmi library
            List<Double> priorValues = new ArrayList<>();
            List<String> variableNames = new ArrayList<>();
            Map<String, String> priorVariables = VariablePrioritizerMap.getPriorVariableForFmu(fmuId);
            for (Map.Entry<String, String> entry : priorVariables.entrySet()) {
                priorValues.add(Double.valueOf(entry.getValue()));
                variableNames.add(entry.getKey());
            }
            double[] arr = priorValues.stream().mapToDouble(d -> d).toArray();
            String[] names = variableNames.toArray(new String[0]);
            simulation.write(names).with(arr);
        }
    }


    public void resetVariablesToDefaultValue(Integer fmuId, Simulation simulation, HashMap<String, FmuSimulationVariableDto> variableDataMap) {
        if (!VariablePrioritizerMap.isSetToDefaultValuesEmpty(fmuId)) {
            String[] variablesToSetToDefault = VariablePrioritizerMap.getVariablesToSetToDefault(fmuId).toArray(new String[0]);
            double[] arr = new double[variablesToSetToDefault.length];
            int index = 0;
            for (String name : variablesToSetToDefault) {
                arr[index] = Double.parseDouble(variableDataMap.get(name).getInitValue());
            }
            simulation.write(variablesToSetToDefault).with(arr);
            VariablePrioritizerMap.emptyVariablesToSetToDefault(fmuId);
        }
    }



    public void runDefaultSimulation(Integer fmuId, List<FmuSimulationVariableDto> fmuVariableList) throws IOException {
        //create a hashmap with variablenames and their data to easily access data log(1) complexicity
        HashMap<String, FmuSimulationVariableDto> variableDataMap = new HashMap<>();
        fmuVariableList.stream()
                .filter(var -> var.isTunnable() ||var.isHasConfiguration())
                .forEach(variable -> variableDataMap.put(variable.getName(), variable));


        List<String> variableNames = fmuVariableList.stream()
                .map(variable -> variable.getName().toString())
                .collect(Collectors.toList());

        ICSVWriter csvWriter = exportToCsv(variableNames);
        //double result = 0;


        // initialisation

        double startTime = 0;
        double stopTime = 50000;
        double stepSize = 1;
        int nbSteps = (int) Math.round(stopTime / stepSize);
        Simulation simulation = new Simulation(fmusFolder + "ControlledTemperature.fmu");


        simulation.init(startTime, stopTime);
        // Convert List<String> to String[]



        //object to be published in the topic
        SimulationValuesDto valuesDto = new SimulationValuesDto();


        //map through list of variables to insert variablenames
        String[] variableNamesArray = fmuVariableList.stream()
                .map(variable -> variable.getName().toString())
                .toList()
                .toArray(new String[0]);

        String[] variableNamesToSend = fmuVariableList.stream()
                .filter(variable -> variable.isTunnable() || variable.isHasConfiguration())
                .map(variable -> {
                    valuesDto.getIds().add(variable.getId());
                    valuesDto.getVariableNames().add(variable.getName());
                    //System.out.println(variable.getName());
                    return variable.getName().toString();
                }).toList().toArray(new String[0]);

        /*
        Simulation
         */
        for (int i = 0; i < nbSteps; i++) {
            simulation.doStep(stepSize);
            double[] values = simulation.read(variableNamesArray).asDoubles();
            //write values in csv
            dataToCSVService.writeValuesInCSV(csvWriter, values, i);

            double[] valuesToSend = simulation.read(variableNamesToSend).asDoubles();
            //write values if prior
            writePriorVariable(fmuId, simulation);
            //reset values if no longer prior
            resetVariablesToDefaultValue(fmuId, simulation, variableDataMap);


            //dataToCsv(variableNamesArray,values,simulation.getCurrentTime());
            valuesDto.setTime(i * stepSize);


            valuesDto.setValues(valuesToSend);
            messagingTemplate.convertAndSend("/topic/greetings", valuesDto);

        }
        System.out.println("--------------valuesDto ------------"+valuesDto);

        csvWriter.close();
        simulation.terminate();

    }

    public void runWithScenario(List<FmuSimulationVariableDto> fmuVariableList, String[] inputVariables, String[] variableNames, List<String[]> values, double startTime, double stopTime, Integer fmuId) throws IOException {

        HashMap<String, FmuSimulationVariableDto> variableDataMap = new HashMap<>();
        fmuVariableList.forEach(variable -> variableDataMap.put(variable.getName(), variable));
        String[] variables = variableDataMap.keySet().stream().toList().toArray(new String[0]);

        ICSVWriter csvWriter = exportToCsv(List.of(variables));

        Simulation simulation = new Simulation(fmusFolder + "ControlledTemperature.fmu");
        simulation.init(startTime, stopTime);
        //object to be published in the topic
        SimulationValuesDto valuesDto = new SimulationValuesDto();
        String[] variableNamesArray = fmuVariableList.stream().map(variable -> {
            valuesDto.getIds().add(variable.getId());
            //System.out.println(variable.getName());
            return variable.getName().toString();
        }).toList().toArray(new String[0]);


        /*
        Simulation
         */

        int l = -1;
        double[] outputValues = new double[0];
        Double stepSize = 0.1;
        // if scenario begin with step 0 , we write the fisrt line
        if (Double.valueOf(values.get(0)[0]) == 0.0) {
            List<String[]> filteredValues = values.stream()
                    .map(row -> Arrays.copyOfRange(row, 1, row.length))  // Map each row to a new array excluding the first element
                    .collect(Collectors.toList());  // Collect the mapped elements into a new list
            l++;
            simulation.write(inputVariables).with(filteredValues.get(l));

        }

        for (int i = 1; i < 2000; i++) {
            int k = 0;
            simulation.doStep(stepSize);
            // Check if current step matches a scenario step
            if (DoubleComparator.compare((double) i, Double.valueOf(values.get(l)[0])) == 0) // if (i==Double.valueOf(values.get(l)[0]))
            {
                l++;
                // Set scenario inputs (assuming values[k].length > 1)
                for (int j = 1; j < values.get(0).length; j++) {

                    simulation.writeVariable(inputVariables[k], values.get(l)[j]);
                    k++;
                }

            }
            outputValues = simulation.read(variableNamesArray).asDoubles();

            //TODO re insert new inputs if scenario exist
            //write values in csv
            dataToCSVService.writeValuesInCSV(csvWriter, outputValues, i);

            //write values if prior
            writePriorVariable(fmuId, simulation);
            //reset values if no longer prior
            resetVariablesToDefaultValue(fmuId, simulation, variableDataMap);
            //TODO send output in topic
            //dataToCsv(variableNamesArray,values,simulation.getCurrentTime());
            valuesDto.setValues(outputValues);
            messagingTemplate.convertAndSend("/topic/greetings", valuesDto);
            System.out.println("going to proceed to next step" + i);

        }
        csvWriter.close();
        simulation.terminate();

    }


}
