package com.example.fmu.fmuImportationMicroservice.models;

import com.example.fmu.fmuImportationMicroservice.dtos.fmu.FmuVariable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Variable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String alias;
    @Column(nullable = false)
    private String causality = "input";


    private String description;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String typeName;

    private String unit;

    private String initValue;

    @Column(nullable = true)
    private String variability = "continuous";



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "config_id")
    private VariableIHMConfiguration variableIHMConfiguration;


    @ManyToOne(fetch = FetchType.LAZY,optional = false ,  cascade = CascadeType.ALL)
    @JoinColumn(name = "fmu_id", nullable = false)
    private Fmu fmu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloc_id")
    private InputVariableBloc inputVariableBloc;




    public Variable(FmuVariable scalarVariable){
        this.causality = scalarVariable.getCausality();
        this.variability = scalarVariable.getVariability();
        this.description = scalarVariable.getDescription();
        this.name = scalarVariable.getName();
        this.typeName = scalarVariable.getTypeName();
        this.initValue = scalarVariable.getInitValue();
        this.variableIHMConfiguration = null;
    }

    @Transient
    public Boolean isTunableVariable(){
        if( this.causality.equals("input") || (this.causality.equals("parameter") && this.variability.equals("tunable"))){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", typeName='" + typeName + '\'' +
                ", causality='" + causality + '\'' +
                ", variability='" + variability + '\'' +
                ", alias='" + alias + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
