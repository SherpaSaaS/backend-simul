package org.example.fmuWindows.eventPublishers;


import org.example.fmuWindows.dtos.PrioritizeVariableEventDto;
import org.example.fmuWindows.events.PriortizeVariableEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PrioritizeVariableEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    public void publishPrioritizeVariableEvent(final String variableName , final Boolean prior){
        PriortizeVariableEvent event = new PriortizeVariableEvent(this , variableName , prior);
        applicationEventPublisher.publishEvent(event);
    }

    public void publishPrioritizeVariableEvent(final String variableName,
                                               final Boolean prior,
                                               final Object value,
                                               final String fmuName,
                                               final Integer variableReference){
        PriortizeVariableEvent event = new PriortizeVariableEvent(
                this ,
                variableName ,
                prior ,
                value ,
                variableReference,
                fmuName
                );
        applicationEventPublisher.publishEvent(event);
    }

    public void publishPrioritizeVariableEvent(PrioritizeVariableEventDto eventDto){
        PriortizeVariableEvent event = new PriortizeVariableEvent(this,
                eventDto.getVariableName(),
                eventDto.getPrior(),
                eventDto.getValue(),
                eventDto.getVariableReference(),
                eventDto.getFmuName());
        applicationEventPublisher.publishEvent(event);
    }
}
