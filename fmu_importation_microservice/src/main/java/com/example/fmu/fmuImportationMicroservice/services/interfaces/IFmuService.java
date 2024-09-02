package com.example.fmu.fmuImportationMicroservice.services.interfaces;

import com.example.fmu.fmuImportationMicroservice.models.Fmu;

import java.util.List;


public interface IFmuService {
    public Fmu save(Fmu fmu);
    public Fmu delete(Fmu fmu);
    public Fmu update(Fmu fmu);
    public Fmu getFmuById(Integer id);
    public List<Fmu> getAll();

}
