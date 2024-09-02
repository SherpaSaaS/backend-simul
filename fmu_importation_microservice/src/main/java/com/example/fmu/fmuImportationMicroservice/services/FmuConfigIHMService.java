package com.example.fmu.fmuImportationMicroservice.services;

import com.example.fmu.fmuImportationMicroservice.models.InputVariableBloc;
import com.example.fmu.fmuImportationMicroservice.models.Variable;
import com.example.fmu.fmuImportationMicroservice.repositories.DictionaryRepository;
import com.example.fmu.fmuImportationMicroservice.repositories.InputBlocRepository;
import com.example.fmu.fmuImportationMicroservice.repositories.VariableRepository;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IFmuConfigIHMService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class FmuConfigIHMService implements IFmuConfigIHMService {
    @Autowired
    VariableRepository variableRepository;
    @Autowired
    InputBlocRepository  inputBlocRepository;

    public void saveBlocsAndVariables(List<InputVariableBloc> blocs) {
        //variableRepository.saveAll(variables);
        inputBlocRepository.saveAll(blocs);

    }

}
