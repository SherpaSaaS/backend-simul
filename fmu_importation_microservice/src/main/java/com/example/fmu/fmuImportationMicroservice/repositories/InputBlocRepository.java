package com.example.fmu.fmuImportationMicroservice.repositories;

import com.example.fmu.fmuImportationMicroservice.models.InputVariableBloc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InputBlocRepository extends JpaRepository<InputVariableBloc, Integer> {

    Optional<InputVariableBloc> findByName(String name);

    @Override
    Optional<InputVariableBloc> findById(Integer integer);
}
