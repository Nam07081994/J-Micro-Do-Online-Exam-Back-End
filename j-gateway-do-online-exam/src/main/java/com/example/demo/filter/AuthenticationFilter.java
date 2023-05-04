package com.example.demo.filter;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.demo.common.JwtTokenUtil;
import com.example.demo.constants.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static com.example.demo.constants.Constant.DETAIL_KEY;
import static com.example.demo.constants.Constant.HTTP_STATUS;
import static com.example.demo.constants.Constant.MESSAGE_ACCESS_DENIED;
import static com.example.demo.constants.Constant.MESSAGE_INVALID_TOKEN;
import static com.example.demo.constants.Constant.MESSAGE_KEY;
import static com.example.demo.constants.Constant.MESSAGE_NOT_LOGIN;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    @Value("${app.url.getEndPoint}")
    private String urlEndPoint;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Autowired
    private RouterValidator validator;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    JSONObject obj = new JSONObject();
                    obj.put(HTTP_STATUS, 401);
                    obj.put(MESSAGE_KEY,MESSAGE_NOT_LOGIN);
                    DataBuffer buffer = response.bufferFactory()
                            .wrap(obj.toJSONString().getBytes(StandardCharsets.UTF_8));

                    return response.writeWith(Mono.just(buffer));
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith(Constant.BEARER_PREFIX)) {
                    authHeader = authHeader.substring(7);
                }
                try {
                    jwtTokenUtil.validateToken(authHeader);
                    ServerHttpRequest request = exchange.getRequest();
                    String endPoint = request.getURI().getPath();
                    var email = jwtTokenUtil.getEmailFromToken(authHeader);
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + authHeader);

                    HttpEntity<Object> entity = new HttpEntity<>(headers);
                    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(urlEndPoint)
                        .queryParam("email", email);
                    String url = builder.toUriString();
                    ResponseEntity<String> endPoints = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                    String responseBody = endPoints.getBody();
                    Set<String> result = null;
                    if(responseBody != null){
                        List<String> stringList = new ObjectMapper().readValue(responseBody, List.class);
                        result = new HashSet<>(stringList);
                    }
                    jwtTokenUtil.authorizeApiCallForUser(authHeader,endPoint, result);
                } catch (JwtException e) {
                    JSONObject obj = new JSONObject();
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    obj.put(HTTP_STATUS, 401);
                    obj.put(MESSAGE_KEY, MESSAGE_INVALID_TOKEN);
                    obj.put(DETAIL_KEY, e.getMessage());

                    if (e instanceof ExpiredJwtException) {
                        return chain.filter(exchange.mutate().build());
                    }
                    DataBuffer buffer = response.bufferFactory()
                            .wrap(obj.toJSONString().getBytes(StandardCharsets.UTF_8));
                    return response.writeWith(Mono.just(buffer));
                } catch (JsonProcessingException e) {
                    JSONObject obj = new JSONObject();
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    obj.put(HTTP_STATUS, 401);
                    obj.put(MESSAGE_KEY, MESSAGE_INVALID_TOKEN);
                    obj.put(DETAIL_KEY, e.getMessage());
                    DataBuffer buffer = response.bufferFactory()
                        .wrap(obj.toJSONString().getBytes(StandardCharsets.UTF_8));
                    return response.writeWith(Mono.just(buffer));
                } catch (AccessDeniedException e){
                    JSONObject obj = new JSONObject();
                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    obj.put(HTTP_STATUS, 403);
                    obj.put(MESSAGE_KEY, MESSAGE_ACCESS_DENIED);
                    obj.put(DETAIL_KEY, e.getMessage());
                    DataBuffer buffer = response.bufferFactory()
                        .wrap(obj.toJSONString().getBytes(StandardCharsets.UTF_8));
                    return response.writeWith(Mono.just(buffer));
                }
            }

            return chain.filter(exchange.mutate().build());
        });
    }

    public static class Config{
    }
}
