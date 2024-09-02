package org.example.apigateway.filter;


import com.netflix.appinfo.InstanceInfo;

import jakarta.ws.rs.ForbiddenException;
import org.example.apigateway.dto.ValidateTokenResponse;
import org.example.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@CrossOrigin(originPatterns = "*")
public class AuthenticationFilter extends AbstractGatewayFilterFactory <AuthenticationFilter.Config>{


    @Autowired
    private RouteValidator validator;

    @Autowired
    private RestTemplate template;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }
    @Autowired
    private DiscoveryClient client;


    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return this.onError(exchange,"missing authorization header", HttpStatus.NOT_FOUND);
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {

                    // get ServiceInstance list using serviceId
                    List<ServiceInstance> siList = client.getInstances("auth-ms");

                    // read manually one instance from index#0
                    ServiceInstance si = siList.get(0);

                    // read URI and Add path that returns url
                    String url = si.getUri() +"/api/auth/validatee?token=" + authHeader;

                    // create object for RestTemplate
                    RestTemplate rt = new RestTemplate();
                    // create headers
                    HttpHeaders  headers = new HttpHeaders();

                    // set `Content-Type` and `Accept` headers
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

                    // example of custom header
                    headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+authHeader);

                    // build the request
                    HttpEntity request = new HttpEntity(headers);


                    // make HTTP call and get Reponse data
                    ResponseEntity<ValidateTokenResponse> response  = rt.exchange(
                            url,
                            HttpMethod.GET,
                            request,
                            ValidateTokenResponse.class,
                            1
                    );

                    if(!Objects.requireNonNull(response.getBody()).getIsValid()){
//                        throw new RuntimeException("unauthorized request");

                        return this.onError(exchange,"unauthorized request", HttpStatus.FORBIDDEN);
                    }

                    // check if role is allowed for the api endpoint
                    boolean allowed = false;
                    if(response.getBody().getRole().equals("USER")){
                      allowed = validator.isUserApi.test(exchange.getRequest());
                    }else{
                        allowed = true;
                    }

                    if(!allowed){
                        return this.onError(exchange,"unauthorized request for this user", HttpStatus.FORBIDDEN);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("invalid access...!"+e.getMessage());
                    return this.onError(exchange,"unauthorized request for this user", HttpStatus.FORBIDDEN);


                }
            }
            return chain.filter(exchange);
        });
    }


    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {

    }
}