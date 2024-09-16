package org.example.fmuWindows.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javafmi.modeldescription.ScalarVariable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Variable {
    private String name;
    private String description;
    private String causality;
    private String typeName;

    public Variable(ScalarVariable scalarVariable){
        this.name = scalarVariable.getName();
        this.causality = scalarVariable.getCausality();
        this.description = scalarVariable.getDescription();
        this.typeName = scalarVariable.getTypeName();
    }

}
