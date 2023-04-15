package com.example.demo.filter;

import java.nio.charset.StandardCharsets;

import com.example.demo.common.JwtTokenUtil;
import com.example.demo.constants.Constant;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.example.demo.constants.Constant.DETAIL_KEY;
import static com.example.demo.constants.Constant.HTTP_STATUS;
import static com.example.demo.constants.Constant.MESSAGE_INVALID_TOKEN;
import static com.example.demo.constants.Constant.MESSAGE_KEY;
import static com.example.demo.constants.Constant.MESSAGE_NOT_LOGIN;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    public AuthenticationFilter() {
        super(Config.class);
    }

    @Autowired
    private RouterValidator validator;

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
                    JwtTokenUtil.validateToken(authHeader);
                } catch (JwtException e) {
                    JSONObject obj = new JSONObject();
                    response.setStatusCode(HttpStatus.BAD_REQUEST);
                    obj.put(HTTP_STATUS, 401);
                    if (e instanceof ExpiredJwtException) {
                        return chain.filter(exchange.mutate().build());
                    }
                    obj.put(MESSAGE_KEY, MESSAGE_INVALID_TOKEN);
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
