package com.example.fmu.fmuImportationMicroservice.services;

import com.example.fmu.fmuImportationMicroservice.models.Fmu;
import com.example.fmu.fmuImportationMicroservice.repositories.FmuRepository;
import com.example.fmu.fmuImportationMicroservice.repositories.VariableRepository;
import com.example.fmu.fmuImportationMicroservice.services.interfaces.IFmuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FmuService implements IFmuService {
    @Autowired
    FmuRepository fmuRepository;

    @Autowired
    VariableRepository variableRepository;


public List<Fmu> getAllFmu(){

        return fmuRepository.findAll();
    }

    @Override
    public Fmu getFmuById(Integer id){
        return fmuRepository.findById(id).orElse(null);
    }

    @Override
    public List<Fmu> getAll() {
        return fmuRepository.findAll();
    }

    @Override
    public Fmu save(Fmu fmu){
        return fmuRepository.save(fmu);
    }

    @Override
    public Fmu delete(Fmu fmu) {
        fmuRepository.delete(fmu);
        return fmu;
    }

    @Override
    public Fmu update(Fmu fmu) {
        return fmuRepository.save(fmu);
    }
}
