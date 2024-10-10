package org.example.fmuWindows.services;


import com.opencsv.CSVWriter;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.example.fmuWindows.dtos.FmuSimulationVariableDto;
import org.example.fmuWindows.dtos.SimulationValuesDto;
import org.example.fmuWindows.eventListeners.PrioritizeVariableEventListner;
import org.example.fmuWindows.models.VariablePrioritizerMap;
import org.example.fmuWindows.utils.DoubleComparator;
import org.javafmi.proxy.FmiProxy;
import org.javafmi.proxy.FmuFile;
import org.javafmi.proxy.ProxyFactory;
import org.javafmi.proxy.v2.FmuProxy;
import org.javafmi.wrapper.Simulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
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



    public void runDefaultSimulation(Integer fmuId, List<FmuSimulationVariableDto> fmuVariableList,  String fmuPath) throws IOException {
        //create a hashmap with variablenames and their data to easily access data log(1) complexicity
        HashMap<String, FmuSimulationVariableDto> variableDataMap = new HashMap<>();
        fmuVariableList.stream()
                .filter(var -> var.isTunnable() ||var.isHasConfiguration())
                .forEach(variable -> variableDataMap.put(variable.getName(), variable));


        System.out.println("JNA Library Path: " + System.getProperty("jna.library.path"));
     //   System.setProperty("jna.library.path",System.getProperty("java.io.tmpdir"));
        //System.out.println("JNA Library Path 22222: " + System.getProperty("jna.library.path"));
        List<String> variableNames = fmuVariableList.stream()
                .map(variable -> variable.getName().toString())
                .collect(Collectors.toList());
      //  System.out.println("-------------- variableNames ------------"+variableNames);

        ICSVWriter csvWriter = exportToCsv(variableNames);
        //double result = 0;


        // initialisation

        double startTime = 0;
        double stopTime = 50000;
        double stepSize = 1;
        int nbSteps = (int) Math.round(stopTime / stepSize);
        System.out.println("************************************fmu path  *************"+fmuPath);

        FmuFile fmuFile=new FmuFile(fmuPath);
        File fileLibrary = new File(fmuFile.getLibraryPath());
        System.out.println("************************************fmu get libreary path *************"+fileLibrary);


   //     String libraryPath = "/tmp/fmu_"+"*"+"/binaries/win64/"+"*"+".dll";
     //   System.setProperty("jna.library.path", libraryPath);

        System.out.println("-------------- new jna lib path   ------------"+System.getProperty("jna.library.path"));

       // NativeLibrary.addSearchPath("*.dll", libraryPath);
      //  System.out.println("-------------- new jna lib path   ------------"+System.getProperty("jna.library.path"));


        System.out.println("--------------------------- class path --------"+System.getProperty("java.class.path"));
        NativeLibrary.addSearchPath("*.dll","/tmp/fmu_\"+\"*\"+\"/binaries/win64/\"+\"*\"+\".dll");
        System.out.println("-------------- new jna lib path   ------------"+System.getProperty("jna.library.path"));
        System.out.println("-------------- user dir   ------------"+System.getProperty("user.dir"));
        System.out.println("--------------  ressource   ------------"+getClass().getResource("/resources/win32-x86-64").getPath());
        System.out.println("--------------  jnidispatch    ------------"+System.getProperty("jnidispatch.path"));

        Simulation simulation = new Simulation(fmuPath);

        System.out.println("--------------simulation  path ------------"+fmusFolder+"----------fmu id --------"+fmuId);

        simulation.init(startTime, stopTime);
        // Convert List<String> to String[]



        //object to be published in the topic
        SimulationValuesDto valuesDto = new SimulationValuesDto();

        System.out.println("--------------valuesDto first------------"+valuesDto);

        //map through list of variables to insert variablenames
        String[] variableNamesArray = fmuVariableList.stream()
                .map(variable -> variable.getName().toString())
                .toList()
                .toArray(new String[0]);

        System.out.println("--------------variableNamesArray ------------"+variableNamesArray);

        String[] variableNamesToSend = fmuVariableList.stream()
                .filter(variable -> variable.isTunnable() || variable.isHasConfiguration())
                .map(variable -> {
                    valuesDto.getIds().add(variable.getId());
                    valuesDto.getVariableNames().add(variable.getName());
                    //System.out.println(variable.getName());
                    return variable.getName().toString();
                }).toList().toArray(new String[0]);
        System.out.println("--------------variableNamesToSend ------------"+variableNamesToSend);

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
