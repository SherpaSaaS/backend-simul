package com.example.fmu.fmuImportationMicroservice.repositories;

import com.example.fmu.fmuImportationMicroservice.models.Variable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VariableRepository extends JpaRepository<Variable, Long> {
    @Override
    Optional<Variable> findById(Long aLong);
}
