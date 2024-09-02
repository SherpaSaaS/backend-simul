package com.example.fmu.fmuDictionaryMicroservice.repositories;

import com.example.fmu.fmuDictionaryMicroservice.models.FmuDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DictionaryRepository extends JpaRepository<FmuDictionary , Integer> {
    Optional<FmuDictionary> findByFmuXmlVariableName(String fmuXmlVariableName);
}
