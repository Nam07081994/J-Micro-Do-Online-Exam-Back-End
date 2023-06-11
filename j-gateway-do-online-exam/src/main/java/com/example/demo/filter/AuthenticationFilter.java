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

@Component
public class AuthenticationFilter
		extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
	@Value("${app.url.getEndPoint}")
	private String urlEndPoint;

	public AuthenticationFilter() {
		super(Config.class);
	}

	@Autowired private RouterValidator validator;

	@Autowired private RestTemplate restTemplate;

	@Autowired private JwtTokenUtil jwtTokenUtil;

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			ServerHttpResponse response = exchange.getResponse();
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			if (validator.isSecured.test(exchange.getRequest())) {
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
					ServerHttpRequest request = exchange.getRequest();
					String endPoint = request.getURI().getPath();
					var roleArrStr = jwtTokenUtil.getInfoFromToken(authHeader, Constant.SUB_KEY);
					if (roleArrStr == null || roleArrStr.length() == 0) {
						response.setStatusCode(HttpStatus.UNAUTHORIZED);
						return response.writeWith(
								Mono.just(
										generateResponse(response, MESSAGE_INVALID_TOKEN, MESSAGE_INVALID_ROLES)));
					}
					HttpHeaders headers = new HttpHeaders();
					headers.add(HttpHeaders.AUTHORIZATION, Constant.BEARER_PREFIX + authHeader);
					HttpEntity<Object> entity = new HttpEntity<>(headers);
					UriComponentsBuilder builder =
							UriComponentsBuilder.fromUriString(urlEndPoint)
									.queryParam(Constant.ROLES_KEY, roleArrStr);
					String url = builder.toUriString();
					ResponseEntity<String> endPoints =
							restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
					String responseBody = endPoints.getBody();

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
}
