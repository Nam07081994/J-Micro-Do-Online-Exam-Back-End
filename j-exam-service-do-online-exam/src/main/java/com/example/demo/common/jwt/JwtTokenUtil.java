package com.example.demo.common.jwt;

import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
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
