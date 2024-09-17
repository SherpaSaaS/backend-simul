package org.example.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    //containing specific API endpoints considered "open" and not requiring authentication
    public static final List<String> openApiEndpoints = List.of(
            "/api/auth",
            "/api/auth/register",
            "/eureka",
            "/api/auth/validatee",
            "/api/auth/forgotPassword",
            "/api/auth/resetPassword"

    );

    public static final List<String> adminApiEndpoints = List.of(
            "/api/fmu/**"
    );

    public static final List<String> userApiEndpoints = List.of(
            "/api/fmu/getAll",
            "/api/simulation/**",
            "/api/simulationWin/**",
            "/api/variable/getFmuVariables/**"
    );
//Predicate named isSecured which is a functional interface used for testing conditions
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri)||request.getURI().getPath().startsWith("/api/auth/forgotPassword")||request.getURI().getPath().startsWith("/api/auth/forgotPassword"));//If the request path doesn't contain any of the open API endpoints (noneMatch returns true)



    public Predicate<ServerHttpRequest> isAdminApi =
            request -> adminApiEndpoints
                    .stream()
                    .anyMatch(uri -> request.getURI().getPath().contains(uri));//If the request path doesn't contain any of the open API endpoints (noneMatch returns true)


    public Predicate<ServerHttpRequest> isUserApi =
            request -> userApiEndpoints
                    .stream()
                    .anyMatch(uri -> {
                        return request.getURI().getPath().contains(uri) || request.getURI().getPath().startsWith(uri.replace("**", ""));
                    });//If the request path doesn't contain any of the open API endpoints (noneMatch returns true)


}
