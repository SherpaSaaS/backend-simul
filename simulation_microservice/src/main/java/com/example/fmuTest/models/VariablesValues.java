package com.example.fmuTest.models;

import java.util.List;

public class VariablesValues {
        private final String[] variableNames;
        private final List<String[]> values;

        public VariablesValues(String[] variableNames, List<String[]> values) {
            this.variableNames = variableNames;
            this.values = values;
        }

        public String[] getVariableNames() {
            return variableNames;
        }

        public List<String[]> getValues() {
            return values;
        }
    }
