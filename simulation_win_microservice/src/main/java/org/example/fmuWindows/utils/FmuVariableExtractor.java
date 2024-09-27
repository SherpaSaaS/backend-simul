package org.example.fmuWindows.utils;

import org.example.fmuWindows.models.Variable;
import org.javafmi.modeldescription.ScalarVariable;
import org.javafmi.wrapper.Simulation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FmuVariableExtractor {
    // causalities
    private static final String CAUSALITY_INPUT = "input";
    private static final String CAUSALITY_OUTPUT = "output";
    private static final String CAUSALITY_INDEPENDENT = "independent";
    private static final String CAUSALITY_PARAMETER = "parameter";
    private static final String CAUSALITY_LOCAL = "local";

    //variabilities
    private static final String VARIABILITY_TUNNABLE = "tunnable";

    public static List<Variable> getAllVariableInputs(Simulation simulation){
        Stream<ScalarVariable> stream = Arrays.stream(simulation.getModelDescription().getModelVariables());
        return stream.filter(variable -> variable.getCausality().equals(CAUSALITY_INPUT) || (variable.getCausality().equals(CAUSALITY_PARAMETER) && variable.getVariability().equals(VARIABILITY_TUNNABLE)))
                .map(Variable::new)
                .collect(Collectors.toList());

    }

    public static List<Variable> getAllVariableOutputs(Simulation simulation){
        Stream<ScalarVariable> stream = Arrays.stream(simulation.getModelDescription().getModelVariables());
        return stream.filter(variable -> !variable.getCausality().equals(CAUSALITY_INPUT) || (variable.getCausality().equals(CAUSALITY_PARAMETER) && variable.getVariability().equals(VARIABILITY_TUNNABLE)))
                .map(Variable::new)
                .collect(Collectors.toList());
    }



}
