package com.example.demo.common.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {
	public String getuserNameFromToken(String token) throws JsonProcessingException {
		Jwt jwt = JwtHelper.decode(token);
		String claims = jwt.getClaims();
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> claimsMap =
				objectMapper.readValue(claims, new TypeReference<Map<String, Object>>() {});
		String userName = claimsMap.get("aud").toString();
		return userName;
	}
}
