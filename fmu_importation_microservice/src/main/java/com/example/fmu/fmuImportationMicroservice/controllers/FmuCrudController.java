package com.example.fmu.fmuImportationMicroservice.controllers;

import com.example.fmu.fmuImportationMicroservice.dtos.*;
import com.example.fmu.fmuImportationMicroservice.dtos.fmu.FmuModelDescription;
import com.example.fmu.fmuImportationMicroservice.models.Fmu;
import com.example.fmu.fmuImportationMicroservice.models.InputVariableBloc;
import com.example.fmu.fmuImportationMicroservice.models.Variable;
import com.example.fmu.fmuImportationMicroservice.repositories.InputBlocRepository;
import com.example.fmu.fmuImportationMicroservice.repositories.VariableRepository;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IFmuConfigIHMService;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IFmuService;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IFmuUploadService;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IVariableService;
import com.example.fmu.fmuImportationMicroservice.utils.FmuHelper;
import com.example.fmu.fmuImportationMicroservice.utils.FmuModelDescriptionReader;
import com.example.fmu.fmuImportationMicroservice.utils.PropertiesAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.NotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.util.*;

import java.util.stream.Collectors;

@Controller
@RestController
@RequestMapping("/api/fmu")
public class FmuCrudController {

    @Value("${fmu.model.image.folder:C:\\Users\\HP\\GitHub\\FMU_prototype\\ReactProject\\front-end\\public\\modelsImages\\}")
    public String FMU_MODEL_IMAGE_FOLDER;

    @Autowired
    IFmuUploadService fmuUploadService;

    @Autowired
    PropertiesAccessor propertiesAccessor;

    @Autowired
    IFmuService fmuService;

    @Autowired
    IVariableService variableService;

    @Autowired
    InputBlocRepository inputBlocRepository;

    @Autowired
    VariableRepository variableRepository;
    @Autowired
    IFmuConfigIHMService fmuConfigIHMService;
    /*
        function to get the fmu List
      */

    @GetMapping("/getAll")

    public ResponseEntity<List<Fmu>> getFmuList() {

        List<Fmu> fmus = null;

            fmus = fmuService.getAll();
            System.out.println("fmuuu " + fmus);

            // Create FmuList with retrieved FMUs (assuming fmus is not null)
            //   FmuList fmuList = new FmuList(null, null, null, true, "Success");
            // fmuList.setsetFmus(fmus); // Set the actual FMU data


        return new ResponseEntity<>(fmus,HttpStatus.OK);
    }

    /*
       function to save the upoloaded model image
     */
    @PostMapping("/uploadModelImage/{id}")
    public ResponseEntity<UploadFmuImageResponse> uploadFmuModelImage(@RequestParam("image")MultipartFile image , @PathVariable(name = "id")Integer fmuId) throws IOException {
        File targetImagePath = new File(propertiesAccessor.getFmuImageFolder()+ "fmu_"+fmuId+"_model_image.png");
        image.transferTo(targetImagePath);
        Fmu fmu = fmuService.getFmuById(fmuId);
        fmu.setModelImagePath(targetImagePath.getPath());
        fmuService.save(fmu);
        return new ResponseEntity<>(new UploadFmuImageResponse(fmuId , Boolean.TRUE),HttpStatus.OK);
    }

    /*
      function to delete the upoloaded model image
    */
    @DeleteMapping("/uploadModelImage/{id}")
    public ResponseEntity<Boolean> deleteFmuImage(@PathVariable(name = "id")Integer fmuId) throws IOException {
        Fmu fmu = fmuService.getFmuById(fmuId);
        if(fmu.getModelImagePath() == null){
            return new ResponseEntity<>(Boolean.TRUE , HttpStatus.OK);
        }
        File image = new File(fmu.getModelImagePath());
        image.delete();
        fmu.setModelImagePath(null);
        fmu.getVariableList().forEach(variable -> {
                variable.setVariableIHMConfiguration(null);
        });
        fmuService.save(fmu);
        return new ResponseEntity<>(Boolean.TRUE,HttpStatus.OK);
    }
    /*
      function to get the data of the fmu
    */
    @GetMapping("/getConfigurationData/{id}")


    public ResponseEntity<ConfigurationInterfaceResponse> getConfigurationData(@PathVariable(name = "id") Integer fmuId){

        Fmu fmu = fmuService.getFmuById(fmuId);
        ConfigurationInterfaceResponse response = ConfigurationInterfaceResponse.merge(fmu);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    /*
      UPLOAD FMU TO a DIRECTORY AND EXTRACT IT
     */
    @PostMapping("/upload")
    public ResponseEntity<FmuUploadResponse> uploadFmu(@RequestParam("file") MultipartFile file) throws NotSupportedException {
        try {

            // UPLOAD FMU TO a DIRECTORY AND EXTRACT IT
            //TODO handle differant possible exception when uploading FMU!
            String targetFile=fmuUploadService.uploadFmu(file);
            System.out.println("targetFile "+targetFile);

            //TODO make FmuModelDescription returned From the upload Service as FMU object class
            String extractedFolder = propertiesAccessor.getExtractedFmusFolder() +
                    Objects.requireNonNull(file.getOriginalFilename()).replace(".fmu", "") +
                    File.separator;


            FmuModelDescription description = FmuModelDescriptionReader.getModelDescriptionFromFmuDescriptionXmlFile(new File(extractedFolder + "modelDescription.xml"));
            if(description != null){
                try{
                    Fmu fmu = Fmu.builder()
                            .id(null)
                            .modelName(description.getModelName())
                            .filePath(targetFile)
                            .variableList(new ArrayList<>())
                            .description(description.getDescription())
                            .fmiVersion(description.getFmiVersion())
                            .inputVariableBlocs(null)
                            .modelImagePath(null)
                            .build();
                    Fmu finalFmu = fmuService.save(fmu);
                    List<Variable> variables = FmuHelper.getVariableList(description , finalFmu , variableService);

                    variables =  variableService.saveAll(variables);

//                    variables.forEach(variable -> {
//                        if(!variable.isTunableVariable()){
//                            variable.setVariableIHMConfiguration(new VariableIHMConfiguration(null,0.0,0.0,50,50,variable));
//                        }else{
//                            variable.setVariableIHMConfiguration(null);
//                        }
//                    });

                    variableService.saveAll(variables);

                    return new ResponseEntity<FmuUploadResponse>(FmuUploadResponse.merge(finalFmu.getId(),description,variables), HttpStatus.OK);

                }catch(Exception e){
                    e.printStackTrace();
                    return new ResponseEntity<>(new FmuUploadResponse(null,null,null , null , null , false , e.getMessage()) , HttpStatus.INTERNAL_SERVER_ERROR);

                }

            }

        } catch (NotSupportedException e) {
            return new ResponseEntity<>(new FmuUploadResponse(null,null , null, null ,null, false , "failed to upload fmu") , HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new FmuUploadResponse(null ,null , null, null , null , false , "failed to upload fmu") , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*
     function to save the configuration data
     */
    @PutMapping("/saveAllConfigurationData/{id}")
    public ResponseEntity<ConfigurationInterfaceResponse> saveAllConfigurationData(@PathVariable(name = "id") Integer fmuId,
                                                                                  @RequestBody ConfigurationInterfaceResponse requestData
    ) throws JsonProcessingException {
       // System.out.println("fmu id  "+ requestData.getFmuId());


        Fmu fmu=fmuService.getFmuById(fmuId);

        List<InputVariableBloc> blocs = fmu.getInputVariableBlocs();

        fmu.setInputVariableBlocs(blocs);

        List<BlocsControllersDto> blocsControllers = requestData.getBlocsControllers();
        System.out.println(requestData.getBlocsControllers());
        List<FmuVariableDto> unlinkedVariables = requestData.getUnlinkedVariables();
        List<FmuVariableDto> outputVatiables = requestData.getOutputsVariables();
       // System.out.println("unlinkedVariables "+ unlinkedVariables);
      //  System.out.println("outputVatiables "+ outputVatiables);
       // System.out.println("blocsControllers "+ blocsControllers);
        List<InputVariableBloc> blocModels = new ArrayList<>();
        for (BlocsControllersDto blocDto : blocsControllers) {
            Optional<InputVariableBloc> blocModel = inputBlocRepository.findById(blocDto.getId());

            blocModel.ifPresentOrElse(existingBloc -> {
                // Code to execute if the Optional has a value (existingBloc)
               // existingBloc.setId(blocDto.getId());
                existingBloc.setName(blocDto.getName());
                System.out.println("blooooooooooooccc modeeeeeel" + blocModel);

                List<Variable> variables = blocDto.getVariableList().stream()
                        .filter(variableDto -> variableRepository.findById(variableDto.getId()).isEmpty()) // Use variableDto directly
                        .map(variableDto -> {
                            Variable newVariable = new Variable();
                            newVariable.setName(variableDto.getName());
                            newVariable.setAlias(variableDto.getAlias());
                            newVariable.setFmu(fmu);
                            newVariable.setTypeName(variableDto.getName()); // Assuming type name is also the name
                            newVariable.setInputVariableBloc(existingBloc);
                            return newVariable;
                        })
                        .collect(Collectors.toList());


                existingBloc.setVariableList(variables);

                blocModels.add(existingBloc);

            }, () -> {
                // Code to execute if the Optional is empty (create new bloc)
                InputVariableBloc newBloc = new InputVariableBloc();
                // ... (Set other matching attributes)
                newBloc.setName(blocDto.getName());

                // ... (Set other properties for newBloc)

                List<Variable> variables = blocDto.getVariableList().stream()
                        .map(variableDto -> {
                            Variable variable = new Variable();
                            // ... (Set properties for new variable)
                            variable.setInputVariableBloc(newBloc);
                            return variable;
                        })
                        .collect(Collectors.toList());

                newBloc.setVariableList(variables);
                System.out.println("newwwBloc VAR LIST " + newBloc.getVariableList());
                blocModels.add(newBloc);
            });
        }


      /*List<Variable> variableModels = new ArrayList<>();
        for (FmuVariableDto variableDto : unlinkedVariables) {
            Variable variableModel = new Variable();
            variableModel.setName(variableDto.getName());
            variableModel.setAlias(variableDto.getAlias());
            variableModel.setFmu(fmu);
            variableModel.setTypeName("string");
          //  System.out.println(("variaaaaaable  " + variableModel));
            variableModels.add(variableModel);
        }
*/

        File targetImagePath = new File(propertiesAccessor.getFmuImageFolder() + "fmu_"+fmuId+"_model_image.png");
        fmu.setModelImagePath(targetImagePath.getPath());
        requestData.getImagePath();
        fmuService.save(fmu);
// Now call the service method with the converted list
        fmuConfigIHMService.saveBlocsAndVariables(blocModels);

        ConfigurationInterfaceResponse response = new ConfigurationInterfaceResponse();


        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/postFmuConfigurationData/{id}")
    public ResponseEntity<ConfigurationInterfaceResponse> postConfigurationData(@PathVariable(name = "id") Integer fmuId,
                                                                                @RequestBody ConfigurationInterfaceResponse requestData
    ) throws JsonProcessingException {

        List<BlocsControllersDto> blocsControllers = requestData.getBlocsControllers();
        List<FmuVariableDto> unlinkedVariables = requestData.getUnlinkedVariables();
        Fmu fmu = fmuService.getFmuById(fmuId);


        // System.out.println("blocsControllers   "+blocsControllers);
        //System.out.println("unlinkedVariables   "+    response.getUnlinkedVariables());

        List<InputVariableBloc> blocModels = new ArrayList<>();
        for (BlocsControllersDto blocDto : blocsControllers) {
            InputVariableBloc blocModel = new InputVariableBloc();//find by id
            blocModel.setId(null); // Assuming these fields have the same name and type
            blocModel.setName(blocDto.getName());

            List<Variable> variables = blocDto.getVariableList().stream()
                    .map(variableDto -> {
                        Variable variable = new Variable();
                        variable.setId(null);
                        variable.setName(variableDto.getName());
                        variable.setAlias(variableDto.getAlias());
                        variable.setFmu(fmu);
                       // variable.setInputVariableBloc(blocModel);
                        variable.setTypeName(variableDto.getName());
                        variable.setInputVariableBloc(blocModel);
                        return variable;
                    })
                    .collect(Collectors.toList());


            blocModel.setVariableList(variables);
            System.out.println("ola " + blocModel.getVariableList());
          //  List<Variable> variableList1 =blocDto.getVariableList()
          //  blocModel.setVariableList(blocDto.getVariableList());

            // ... (Set other matching attributes)
            blocModels.add(blocModel);
        }
        List<Variable> variableModels = new ArrayList<>();
        for (FmuVariableDto variableDto : unlinkedVariables) {
            Variable variableModel = new Variable();
            variableModel.setId(null); // Assuming these fields have the same name and type
            variableModel.setName(variableDto.getName());
            variableModel.setAlias(variableDto.getAlias());
            variableModel.setFmu(fmu);
            variableModel.setTypeName("string");



            System.out.println(("variaaaaaable  " + variableModel));

            // ... (Set other matching attributes)
            variableModels.add(variableModel);
        }


// Now call the service method with the converted list
        fmuConfigIHMService.saveBlocsAndVariables(blocModels);

        ConfigurationInterfaceResponse response = new ConfigurationInterfaceResponse();

        response.setBlocsControllers((List<BlocsControllersDto>) blocsControllers);
        response.setUnlinkedVariables(unlinkedVariables);
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
