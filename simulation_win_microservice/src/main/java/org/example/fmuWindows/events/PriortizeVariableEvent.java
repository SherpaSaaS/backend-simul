package org.example.fmuWindows.events;

import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class PriortizeVariableEvent extends ApplicationEvent {
    private String variableName;
    private Boolean prior;
    private Object value;
    private Integer variableReference;
    private String fmuName;


    public PriortizeVariableEvent(Object source , String variableName , Boolean prior , Object value , Integer variableReference, String fmuName) {
        super(source);
        this.variableName = variableName;
        this.prior = prior;
        this.variableReference = variableReference;
        this.fmuName = fmuName;
        this.value = value;
    }

    public PriortizeVariableEvent(Object source , String variableName , Boolean prior) {
        super(source);
        this.variableName = variableName;
        this.prior = prior;
    }

    public PriortizeVariableEvent(Object source, Clock clock) {
        super(source, clock);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer getVariableReference() {
        return variableReference;
    }

    public void setVariableReference(Integer variableReference) {
        this.variableReference = variableReference;
    }

    public String getFmuName() {
        return fmuName;
    }

    public void setFmuName(String fmuName) {
        this.fmuName = fmuName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public Boolean getPrior() {
        return prior;
    }

    public void setPrior(Boolean prior) {
        this.prior = prior;
    }

}
