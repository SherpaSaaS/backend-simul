package com.example.fmuTest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javafmi.modeldescription.ScalarVariable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Input extends Variable{
    private Double time;
    private Double value;

    public Input(ScalarVariable scalarVariable){
        super(scalarVariable);
        this.setCausality("input");
        this.time = 0.0;
        this.value = 0.0;
    }

    public Input(ScalarVariable scalarVariable , Double time , Double value){
        super(scalarVariable);
        this.setCausality("input");
        this.time = time;
        this.value = value;
    }

}
