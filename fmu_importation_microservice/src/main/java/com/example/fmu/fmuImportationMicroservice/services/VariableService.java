package com.example.fmu.fmuImportationMicroservice.services;

import com.example.fmu.fmuImportationMicroservice.dtos.fmu.FmuVariable;
import com.example.fmu.fmuImportationMicroservice.models.Fmu;
import com.example.fmu.fmuImportationMicroservice.models.FmuDictionary;
import com.example.fmu.fmuImportationMicroservice.models.InputVariableBloc;
import com.example.fmu.fmuImportationMicroservice.models.Variable;
import com.example.fmu.fmuImportationMicroservice.repositories.DictionaryRepository;
import com.example.fmu.fmuImportationMicroservice.repositories.InputBlocRepository;
import com.example.fmu.fmuImportationMicroservice.repositories.VariableRepository;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VariableService implements IVariableService {
    @Autowired
    VariableRepository variableRepository;

    @Autowired
    FmuService fmuService;

    @Autowired
    DictionaryRepository dictionaryRepository;

    @Autowired
    InputBlocRepository inputBlocRepository;

    @Override
    public Variable getVariableById(Long id){
        Optional<Variable> optional = variableRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public InputVariableBloc getVariableBlocFromDictionary(Fmu fmu , FmuVariable var){
        InputVariableBloc inputVariableBlocFinal;
        Optional<FmuDictionary> fmuDictionaryOptional = dictionaryRepository.findByFmuXmlVariableName(var.getName());
        if(fmuDictionaryOptional.isPresent()){
            Optional<InputVariableBloc> inputVariableBlocOptional = inputBlocRepository.findByName(fmuDictionaryOptional.get().getBlocName());
            if(inputVariableBlocOptional.isEmpty()){
                inputVariableBlocFinal = inputBlocRepository.save(
                        InputVariableBloc.builder()
                                .fmu(new ArrayList<>(Set.of(fmu)))
                                .name(fmuDictionaryOptional.get().getBlocName())
                                .variableList(new ArrayList<>())
                                .build()
                );
                if (fmu.getInputVariableBlocs()==null)
                {
                    fmu.setInputVariableBlocs(new ArrayList<>());
                }

                fmu.getInputVariableBlocs().add(inputVariableBlocFinal);
                fmuService.save(fmu);

            }else{
                inputVariableBlocFinal = inputVariableBlocOptional.get();
            }
        }else{
            inputVariableBlocFinal = null;
        }

        return inputVariableBlocFinal;
    }


    @Override
    public List<Variable> saveVariablesFromFmu(File fmuFile) {
        return null;
    }

    @Override
    public Variable update(Variable variable) {
        return null;
    }

    @Override
    public List<Variable> saveAll(List<Variable> variables){
        return variableRepository.saveAll(variables);
    }

    @Override
    public Variable save(Variable variable){
        return variableRepository.save(variable);
    }


}
