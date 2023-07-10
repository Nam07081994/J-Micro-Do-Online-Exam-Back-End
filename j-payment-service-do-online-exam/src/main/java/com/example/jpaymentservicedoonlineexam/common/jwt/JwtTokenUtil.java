package com.example.jpaymentservicedoonlineexam.common.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;

import java.util.Map;

public class JwtTokenUtil {
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
