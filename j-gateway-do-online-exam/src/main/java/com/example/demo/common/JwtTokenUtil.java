package com.example.demo.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {
	@Value("${app.secret}")
	private String jwtSecret;

	public void validateToken(final String token) throws JwtException {
		Jwts.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getExpiration()
				.before(new Date());
	}

	public void authorizeApiCallForUser(String currentEndPoint, Set<String> endPoints)
			throws AccessDeniedException {
		if (!endPoints.contains(currentEndPoint)) {
			throw new AccessDeniedException("User don't have permission to access this api");
		}
	}

	public String getInfoFromToken(String token, String key) throws JsonProcessingException {
		Jwt jwt = JwtHelper.decode(token);
		String claims = jwt.getClaims();
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> claimsMap = objectMapper.readValue(claims, new TypeReference<>() {});

		return claimsMap.get(key).toString();
	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
