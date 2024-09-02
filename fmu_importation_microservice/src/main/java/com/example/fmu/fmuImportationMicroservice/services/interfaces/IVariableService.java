package com.example.fmu.fmuImportationMicroservice.services.interfaces;

import com.example.fmu.fmuImportationMicroservice.dtos.fmu.FmuVariable;
import com.example.fmu.fmuImportationMicroservice.models.Fmu;
import com.example.fmu.fmuImportationMicroservice.models.InputVariableBloc;
import com.example.fmu.fmuImportationMicroservice.models.Variable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;


public interface IVariableService {

    public Variable getVariableById(Long id);

    public InputVariableBloc getVariableBlocFromDictionary(Fmu fmu , FmuVariable var);

    public List<Variable> saveVariablesFromFmu(File fmuFile);

    public Variable update(Variable variable);

    public List<Variable> saveAll(List<Variable> variables);

    public Variable save(Variable variable);
}
