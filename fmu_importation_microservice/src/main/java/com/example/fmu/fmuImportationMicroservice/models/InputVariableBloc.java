package com.example.fmu.fmuImportationMicroservice.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "input_variable_bloc")
public class InputVariableBloc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToMany(mappedBy = "inputVariableBlocs" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    private List<Fmu> fmu;



    @OneToMany(mappedBy = "inputVariableBloc", fetch = FetchType.LAZY , cascade = CascadeType.MERGE)
    private List<Variable> variableList;


    @Override
    public String toString() {
        return "InputVariableBloc{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
