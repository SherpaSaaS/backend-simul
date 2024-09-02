package org.sherpaengineering.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sherpaengineering.dtos.MappedSimulationValuesDto;
import org.sherpaengineering.dtos.SimulationValuesDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.DecimalFormat;

@Service
public class WebSocketHandler extends StompSessionHandlerAdapter {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("connected to web socket");


        session.subscribe("/topic/greetings", this);

    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println("exception");
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.out.println("transport error");
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return SimulationValuesDto.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        SimulationValuesDto msg = (SimulationValuesDto) payload;
        System.out.println("Received : " + msg.getTime());
        MappedSimulationValuesDto mappedSimulationValuesDto = new MappedSimulationValuesDto();
        for(int i=0 ; i < msg.getIds().size() ; i++){
            mappedSimulationValuesDto
                    .getVariableValueMap()
                    .put(
                            msg.getIds().get(i),
                            Double.valueOf(
                                    String.format("%.4f%n", msg.getValues()[i])
                                            .replaceAll(",", ".")
                            )
                    );
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String dataToSend = objectMapper.writeValueAsString(mappedSimulationValuesDto);
            messagingTemplate.convertAndSend("/topic/greetings", dataToSend);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
