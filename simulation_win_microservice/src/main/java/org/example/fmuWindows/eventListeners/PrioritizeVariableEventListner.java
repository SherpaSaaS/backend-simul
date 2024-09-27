package org.example.fmuWindows.eventListeners;

import org.example.fmuWindows.events.PriortizeVariableEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PrioritizeVariableEventListner implements ApplicationListener<PriortizeVariableEvent> {

    private static final Map<String , Map<Integer , PriortizeVariableEvent>> eventMapper = new HashMap<>();

    @Override
    public void onApplicationEvent(PriortizeVariableEvent event ) {
        String key = event.getFmuName() + event.getVariableName() + event.getVariableReference();
        if(event.getPrior().equals(Boolean.TRUE)){
            eventMapper.computeIfAbsent(key, k -> new HashMap<Integer, PriortizeVariableEvent>());
            eventMapper.get(key).put(event.getVariableReference() , event);
        }else{
            if(eventMapper.get(key) != null){
                if(eventMapper.get(key).get(event.getVariableReference()) != null){
                    eventMapper.get(key).remove(event.getVariableReference());
                }
            }
        }


    }


    public static PriortizeVariableEvent getEventInfo(String fmuName , String variableName , Integer variableReference){
        String key = fmuName + variableName + variableReference;
        if(eventMapper.get(key) == null){
            return null;
        }
        return eventMapper.get(key).get(variableReference);
    }



    public static Object isPriorized(String fmuName , String variableName , Integer variableReference){
        String key = fmuName + variableName + variableReference;
        if(eventMapper.get(key) == null){
            return null;
        }
        if(eventMapper.get(key).get(variableReference).getPrior().equals(Boolean.TRUE)){
            return eventMapper.get(key).get(variableReference).getValue();
        }
        return null;
    }
}
