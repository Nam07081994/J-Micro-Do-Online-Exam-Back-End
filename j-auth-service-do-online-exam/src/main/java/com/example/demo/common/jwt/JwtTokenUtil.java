package com.example.demo.common.jwt;

import com.example.demo.constant.StringConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

	public static String getTokenFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(StringConstant.AUTHENTICATION_KEY);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(StringConstant.BEARER_KEY)) {
			return bearerToken.substring(7);
		}

		return null;
	}

	public static boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public static Authentication getAuthenticationFromToken(String token) {
		final Claims claims =
				Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
		final String email = claims.getSubject();
		if (email != null) {
			return new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
		}
		return null;
	}

	public static Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public static Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
	}

	public static Object getUserInfoFromToken(String token, Function<Claims, Object> claimsResolver) {
		token = token.substring(7);
		return extractClaim(token, claimsResolver);
	}

	public static boolean isTokenExpired(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
			Date expiration = claims.getExpiration();
			return expiration.before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	public static String getUserInfoFromToken1(String token, String key)
			throws JsonProcessingException {
		Jwt jwt = JwtHelper.decode(token);
		String claims = jwt.getClaims();
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> claimsMap = objectMapper.readValue(claims, new TypeReference<>() {});

		return claimsMap.get(key).toString();
	}
}
