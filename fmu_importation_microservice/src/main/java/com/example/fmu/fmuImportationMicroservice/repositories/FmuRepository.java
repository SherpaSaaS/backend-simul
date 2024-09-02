package com.example.fmu.fmuImportationMicroservice.repositories;

import com.example.fmu.fmuImportationMicroservice.models.Fmu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FmuRepository extends JpaRepository<Fmu, Integer> {

    Optional<Fmu> findByModelName(String modelName);
}
