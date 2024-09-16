package org.example.fmuWindows.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javafmi.wrapper.Simulation;
import org.javafmi.wrapper.variables.SingleRead;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariableAccessor {
    private Simulation simulation;

    public static final String VARIABLE_TYPE_DOUBLE = "real";
    public static final String VARIABLE_TYPE_BOOLEAN = "boolean";
    public static final String VARIABLE_TYPE_STRING = "string";
    public static final String VARIABLE_TYPE_ENUMERATE = "enumerate";

    public static void write(String variableName, String typeName, Object value , Simulation simulation) throws Exception {
        Simulation.WriteCall writer = simulation.write(variableName);

        if(value instanceof Double && VARIABLE_TYPE_DOUBLE.equals(typeName)){
            writer.with((Double)value);
        }else if(value instanceof Boolean && VARIABLE_TYPE_BOOLEAN.equals(typeName)){
            writer.with((Boolean) value);
        }else if(value instanceof String && VARIABLE_TYPE_STRING.equals(typeName)) {
            writer.with((String) value);
        }else{
            throw new Exception("type not supported");
        }
    }


    public static Object read(String scalarVariableName, String typeName , Simulation simulation ) throws Exception {
         SingleRead reader = simulation.read(scalarVariableName);

        if(VARIABLE_TYPE_DOUBLE.equals(typeName)){
            return reader.asDouble();
        }else if(VARIABLE_TYPE_BOOLEAN.equals(typeName)){
            return reader.asBoolean();
        }else if(VARIABLE_TYPE_STRING.equals(typeName)) {
            return reader.asString();
        }else{
            throw new Exception("type not supported");
        }
    }

    public static <T> T castObject(Object obj, String typeName, Class<T> targetType) {
        try {
            return targetType.cast(obj);
        } catch (ClassCastException e) {
            // Handle the exception or return null as needed
            return null;
        }
    }
}
