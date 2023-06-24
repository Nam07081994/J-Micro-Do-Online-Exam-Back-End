package com.example.demo.config.security.SecurityCustom.Filter;

import static com.example.demo.constant.StringConstant.USERNAME_TOKEN_KEY;

import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.config.jpa.JpaConfig;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException, java.io.IOException {
		String token = JwtTokenUtil.getTokenFromRequest(request);

		if (token != null && JwtTokenUtil.validateToken(token)) {
			Authentication auth = JwtTokenUtil.getAuthenticationFromToken(token);
			JpaConfig.setRegisteredUser(JwtTokenUtil.getUserInfoFromToken1(token, USERNAME_TOKEN_KEY));
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		chain.doFilter(request, response);
	}
}
