package com.example.demo.filter;

import static com.example.demo.constants.Constant.*;

import com.example.demo.common.JwtTokenUtil;
import com.example.demo.constants.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter
		extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

	@Value("${app.url.getEndPoint}")
	private String urlEndPoint;

	@Value("${app.url.get-image-pattern}")
	private String FETCH_IMAGE_ENDPOINT_PATTERN;

	public AuthenticationFilter() {
		super(Config.class);
	}

	@Autowired private RestTemplate restTemplate;

	@Autowired private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			ServerHttpResponse response = exchange.getResponse();
			String endPoint = exchange.getRequest().getURI().getPath();
			Pattern patternDate = Pattern.compile(FETCH_IMAGE_ENDPOINT_PATTERN);
			Matcher matcher = patternDate.matcher(endPoint);
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			if (!matcher.matches() && !getEndpointsByRoleFromCache(PUBLIC_ROLE, EMPTY_STRING).contains(endPoint)) {
				if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					response.setStatusCode(HttpStatus.UNAUTHORIZED);
					return response.writeWith(
							Mono.just(generateResponse(response, MESSAGE_NOT_LOGIN, Constant.EMPTY_STRING)));
				}

				try {
					String authHeader =
							exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
					if (authHeader != null && authHeader.startsWith(Constant.BEARER_PREFIX)) {
						authHeader = authHeader.substring(7);
					}

					jwtTokenUtil.validateToken(authHeader);
					var roleArrStr = jwtTokenUtil.getInfoFromToken(authHeader, Constant.SUB_KEY);
					if (roleArrStr == null || roleArrStr.length() == 0) {
						response.setStatusCode(HttpStatus.UNAUTHORIZED);
						return response.writeWith(
								Mono.just(
										generateResponse(response, MESSAGE_INVALID_TOKEN, MESSAGE_INVALID_ROLES)));
					}

					String responseBody = getEndpointsByRoleFromCache(roleArrStr, Constant.BEARER_PREFIX + authHeader);

					if (responseBody != null) {
						List<String> stringList = new ObjectMapper().readValue(responseBody, List.class);
						jwtTokenUtil.authorizeApiCallForUser(endPoint, new HashSet<>(stringList));
					}
				} catch (JwtException e) {
					if (e instanceof ExpiredJwtException) {
						return chain.filter(exchange.mutate().build());
					}
					response.setStatusCode(HttpStatus.UNAUTHORIZED);
					return response.writeWith(
							Mono.just(generateResponse(response, MESSAGE_INVALID_TOKEN, e.getMessage())));
				} catch (JsonProcessingException e) {
					response.setStatusCode(HttpStatus.UNAUTHORIZED);
					return response.writeWith(
							Mono.just(generateResponse(response, MESSAGE_INVALID_TOKEN, e.getMessage())));
				} catch (AccessDeniedException e) {
					response.setStatusCode(HttpStatus.FORBIDDEN);
					return response.writeWith(
							Mono.just(generateResponse(response, MESSAGE_ACCESS_DENIED, e.getMessage())));
				} catch (Exception e) {
					response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
					return response.writeWith(
							Mono.just(generateResponse(response, MESSAGE_SERVER_NOT_READY, e.getMessage())));
				}
			}

			return chain.filter(exchange.mutate().build());
		});
	}

	public static class Config {}

	private DataBuffer generateResponse(ServerHttpResponse response, String msg, String detailMsg) {
		JSONObject obj = new JSONObject();
		obj.put(MESSAGE_KEY, msg);
		obj.put(DETAIL_KEY, detailMsg);

		return response.bufferFactory().wrap(obj.toJSONString().getBytes(StandardCharsets.UTF_8));
	}

	private String getEndpointsByRole(String roles, String token) {
		HttpHeaders headers = new HttpHeaders();
		if (!token.isEmpty()) {
			headers.add(HttpHeaders.AUTHORIZATION, token);
		}
		HttpEntity<Object> entity = new HttpEntity<>(headers);
		UriComponentsBuilder builder =
				UriComponentsBuilder.fromUriString(urlEndPoint).queryParam(Constant.ROLES_KEY, roles);
		String url = builder.toUriString();
		ResponseEntity<String> endPoints =
				restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		return endPoints.getBody();
	}


    public String getEndpointsByRoleFromCache(String role, String authHeader) {
        String cacheKey = generateCacheKey(role);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // Kiểm tra cache
        if (redisTemplate.hasKey(cacheKey)) {
            // Cache hit, trả về kết quả từ cache
            return (String) valueOperations.get(cacheKey);
        } else {
            // Cache miss, gọi API và lưu kết quả vào cache
            String responseBody = getEndpointsByRole(role, authHeader);
            valueOperations.set(cacheKey, responseBody);
            return responseBody;
        }
    }

    private String generateCacheKey(String role) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(role);
        return keyBuilder.toString();
    }
}
