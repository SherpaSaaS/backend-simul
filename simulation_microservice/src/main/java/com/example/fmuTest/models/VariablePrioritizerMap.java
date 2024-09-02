package com.example.fmuTest.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariablePrioritizerMap {
    public static final Map<Integer , HashMap<String , String>> variabelPriorities = new HashMap<>();
    public static final Map<Integer , List<String>> variablesToSetToDefault = new HashMap<>();

    public static List<String> getVariablesToSetToDefault(Integer fmuId){
        return variablesToSetToDefault.get(fmuId);
    }
    public static boolean isSetToDefaultValuesEmpty(Integer fmuId){
        if(variabelPriorities.get(fmuId) != null) {
            return variablesToSetToDefault.get(fmuId).isEmpty();
        }else{
            throw new RuntimeException("fmu not running");
        }
    }

    public static boolean isPriorVariablesEmpty(Integer fmuId){
        if(variabelPriorities.get(fmuId) != null) {
            return variabelPriorities.get(fmuId).isEmpty();
        }else{
            throw new RuntimeException("fmu not running");
        }
    }

    public static Map<String , String> getPriorVariableForFmu(Integer fmuId){
        return variabelPriorities.get(fmuId);
    }

    public static void addFmuEntryInMap(Integer fmuId){
        variabelPriorities.put(fmuId , new HashMap<>());
        variablesToSetToDefault.put(fmuId , new ArrayList<>());
    }

    public static void  prioritizeVariable(Integer fmuId , String variableName , String value){
        if(variabelPriorities.get(fmuId) != null) {
            variabelPriorities.get(fmuId).put(variableName , value);
        }else{
            throw new RuntimeException("fmu not running");
        }
    }

    public static void unprioritizeVariable(Integer fmuId , String variableName){
        if(variabelPriorities.get(fmuId) != null) {
            if(variabelPriorities.get(fmuId).get(variableName) != null){
                variabelPriorities.get(fmuId).remove(variableName);
                variablesToSetToDefault.get(fmuId).add(variableName);
            }
        }else{
            throw new RuntimeException("fmu not running");
        }
    }

    public static void removeVariableFromToSetToDefault(Integer fmuId,String variableName){
        variablesToSetToDefault.get(fmuId).remove(variableName);
    }

    public static void emptyVariablesToSetToDefault(Integer fmuId){
        variablesToSetToDefault.get(fmuId).removeAll(variablesToSetToDefault.get(fmuId));
    }

    public static void removeFmuEntryFromMap(Integer fmuId){
        variabelPriorities.remove(fmuId);
        variablesToSetToDefault.remove(fmuId);
    }

    public static Object readVariableValue(Integer fmuId , String variableName , String variableTypeName){
        if(variabelPriorities.get(fmuId) == null){
            throw new RuntimeException("fmu not running");
        }
        if(variabelPriorities.get(fmuId).get(variableName) == null){
            throw new RuntimeException("variable not in map");
        }
        String value = variabelPriorities.get(fmuId).get(variableName);
        switch (variableTypeName) {
            case "real":
                return Double.valueOf(value);
            case "boolean":
                if (value.equals("0")) {
                    return Boolean.FALSE;
                } else if (value.equals("1")) {
                    return Boolean.TRUE;
                } else {
                    throw new RuntimeException("illegal boolean value");
                }
            case "integer":
                return Integer.valueOf(value);
            default:
                return null;
        }
    }
}
