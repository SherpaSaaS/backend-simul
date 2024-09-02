package com.example.fmu.fmuImportationMicroservice.repositories;

import com.example.fmu.fmuImportationMicroservice.models.FmuDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryRepository extends JpaRepository<FmuDictionary, Integer> {

    Optional<FmuDictionary> findByFmuXmlVariableName(String fmuXmlVariableName);
}
