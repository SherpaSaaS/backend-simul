package org.sherpaengineering.configurations;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import org.sherpaengineering.services.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebSocketClientConfiguration {

    private final String platform = "windows";

    @Autowired
    public WebSocketClientConfiguration(WebSocketHandler webSocketHandler , DiscoveryClient client) {
        List<ServiceInstance> siList;
        if (platform=="windows")
        {
             siList = client.getInstances("simulation-win-ms");
        }
        else{
            siList = client.getInstances("simulation-ms");
        }
       
        ServiceInstance si = siList.get(0);

        String webSocketUrl = si.getUri().toString().replace("http","ws") + "/simulation/websocket";
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//        container.setDefaultMaxBinaryMessageBufferSize(1024*1024);
//        container.setDefaultMaxTextMessageBufferSize(1024*1024);
        WebSocketClient transport = new StandardWebSocketClient(container);


        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        stompClient.connect("ws://localhost:8081/websocket", webSocketHandler);
        stompClient.connect(webSocketUrl, webSocketHandler);
    }
}
