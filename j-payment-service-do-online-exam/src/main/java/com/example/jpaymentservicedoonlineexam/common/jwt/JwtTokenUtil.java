package com.example.jpaymentservicedoonlineexam.common.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class JwtTokenUtil {

    private static String jwtSecret;
    private static Long expiration;

    @Value("${app.jwt.secret}")
    public void setJwtSecret(String jwtSecret) {
        JwtTokenUtil.jwtSecret = jwtSecret;
    }

    @Value("${app.jwt.expiration}")
    public void setExpriration(Long expiration) {
        JwtTokenUtil.expiration = expiration;
    }

    public static String generateToken(String username, String email, String role, String userID) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        return createToken(claims, email, role, userID);
    }

    private static String createToken(
            Map<String, Object> claims, String email, String roles, String userID) {
        return Jwts.builder()
                .setClaims(claims)
                .setId(email)
                .setSubject(roles)
                .setAudience(userID)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public static String getuserNameFromToken(String token) throws JsonProcessingException {
        Jwt jwt = JwtHelper.decode(token);
        String claims = jwt.getClaims();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> claimsMap =
                objectMapper.readValue(claims, new TypeReference<Map<String, Object>>() {});
        return claimsMap.get("aud").toString();
    }

    public static String getUserInfoFromToken(String token, String key)
            throws JsonProcessingException {
        Jwt jwt = JwtHelper.decode(token);
        String claims = jwt.getClaims();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> claimsMap = objectMapper.readValue(claims, new TypeReference<>() {});
        return claimsMap.get(key).toString();
    }

    public static String getTokenWithoutBearer(String token) {
        return token.substring(7);
    }
}
