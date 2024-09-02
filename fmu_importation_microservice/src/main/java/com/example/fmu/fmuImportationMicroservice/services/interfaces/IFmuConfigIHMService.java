package com.example.fmu.fmuImportationMicroservice.services.interfaces;

import com.example.fmu.fmuImportationMicroservice.models.InputVariableBloc;

import java.util.List;

public interface IFmuConfigIHMService {
public void saveBlocsAndVariables(List<InputVariableBloc> blocs);
}
