package com.example.demo.service;

import static com.example.demo.constant.StringConstant.COMMA_STRING_CHARACTER;
import static com.example.demo.constant.TranslationCodeConstant.*;
import java.util.*;
import java.util.stream.Collectors;

import com.example.demo.command.LoginCommand;
import com.example.demo.command.RegisterCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.response.CommonResponse;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.config.jpa.JpaConfig;
import com.example.demo.constant.StringConstant;
import com.example.demo.entity.User;
import com.example.demo.repository.EndPointRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import java.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.constant.TranslationCodeConstant.*;

@Service
public class AuthenticationService {

	@Value("${app.default-user-thumbnail}")
	private String DEFAULT_USER_THUMBNAIL_URI;

	@Value("${app.file-service-endpoint}")
	private String UPDATE_THUMBNAIL_URI;

	@Autowired private UserRepository userRepository;

	@Autowired private RoleRepository roleRepository;

	@Autowired private EndPointRepository endPointRepository;

	@Autowired private PasswordEncoder passwordEncoder;

	@Autowired private RestTemplate restTemplate;

	@Autowired private TranslationService translationService;

	@Autowired private AuthenticationManager authenticationManager;

	public ResponseEntity<?> resister(RegisterCommand command) {
		var userExist = userRepository.findByEmail(command.getEmail());
		var roleUser = roleRepository.findByRoleName(StringConstant.USER_ROLE_STRING);

		if (userExist.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_EMAIL_EXIST));
		}

		if (roleUser.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_ROLE_USER));
		}

		var user =
				User.builder()
						.userName(command.getUserName())
						.email(command.getEmail())
						.thumbnail(DEFAULT_USER_THUMBNAIL_URI)
						.password(passwordEncoder.encode(command.getPassword()))
						.roles(List.of(roleUser.get().getId()))
						.build();

		JpaConfig.setRegisteredUser(user.getUserName());
		userRepository.save(user);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(REGISTER_INFORMATION_SUCCESS));
	}

	public ResponseEntity<?> login(LoginCommand command) {
		Authentication authentication =
				authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(command.getEmail(), command.getPassword()));
		Optional<User> userOpt = userRepository.findByEmail(command.getEmail());

		if (authentication.isAuthenticated() && userOpt.isPresent()) {
			var userName = userOpt.get().getUserName();
			String roles = StringConstant.EMPTY_STRING;
			for (Long roleID : userOpt.get().getRoles()) {
				roles =
						roles + roleRepository.findById(roleID).get().getRoleName() + COMMA_STRING_CHARACTER;
			}

			return GenerateResponseHelper.generateDataResponse(
					HttpStatus.OK,
					Map.of(
							StringConstant.ACCESS_TOKEN_KEY,
							JwtTokenUtil.generateToken(
									command.getEmail(), roles.substring(0, roles.length() - 1), userName)));
		}

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.UNAUTHORIZED, translationService.getTranslation(INVALID_LOGIN_INFORMATION));
	}

	public Set<String> getEndPoint(String roles) {
		Set<String> endPoints = new HashSet<>();
		String[] arrRoles = roles.split(COMMA_STRING_CHARACTER);

		for (String role : arrRoles) {
			var roleEndpointID = roleRepository.findByRoleName(role).get().getEndPoint();
			List<String> roleNames =
					roleEndpointID.stream()
							.map((id) -> endPointRepository.findById(id).get().getEndPoint())
							.toList();
			endPoints.addAll(roleNames);
		}

		return endPoints;
	}

	public ResponseEntity<?> updateUserThumbnail(String token, MultipartFile file) {
		try {
			String email = (String) JwtTokenUtil.getUserInfoFromToken(token, Claims::getId);
			Optional<User> userOptional = userRepository.findByEmail(email);

			if (userOptional.isEmpty()) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_USER_INFORMATION));
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add(StringConstant.DOMAIN_KEY, StringConstant.USER_DOMAIN_NAME);
			body.add(StringConstant.FILE_TYPE_KEY, StringConstant.IMAGE_FOLDER_TYPE);
			ByteArrayResource contentsAsResource =
					new ByteArrayResource(file.getBytes()) {
						@Override
						public String getFilename() {
							return file.getOriginalFilename();
						}
					};
			body.add(StringConstant.FILE_KEY, contentsAsResource);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			ResponseEntity<String> response =
					restTemplate.exchange(UPDATE_THUMBNAIL_URI, HttpMethod.POST, requestEntity, String.class);

			// update new user thumbnail
			userOptional.get().setThumbnail(response.getBody());
			userRepository.save(userOptional.get());

			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.OK, translationService.getTranslation(UPDATE_USER_THUMBNAIL_SUCCESS));
		} catch (Exception ex) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(ERROR_UPDATE_USER_THUMBNAIL));
		}
	}

	public ResponseEntity<?> getUserInfo(String token) {
		try {
			String email = (String) JwtTokenUtil.getUserInfoFromToken(token, Claims::getId);
			Optional<User> userOptional = userRepository.findByEmail(email);
			if (userOptional.isEmpty()) {
				throw new Exception(ERROR_GET_USER_INFOR);
			}
			// TODO convert user -> user dto
			return GenerateResponseHelper.generateDataResponse(
					HttpStatus.OK, Map.of(StringConstant.DATA_KEY, userOptional.get()));
		} catch (Exception ex) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(ERROR_GET_USER_INFOR));
		}
	}

    public ResponseEntity<?> refreshToken(String token) throws JsonProcessingException {
        var tokenExpired = JwtTokenUtil.isTokenExpired(token);
        if (!tokenExpired) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", translationService.getTranslation(INVALID_TOKEN_INFORMATION)))
                    .build()
                    .getBody());
        }
        var email = JwtTokenUtil.getEmailFromToken(token);
        var userRoles = userRepository.findByEmail(email);
        if (!userRoles.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", translationService.getTranslation(INVALID_USER_DUPLICATE_INFORMATION)))
                    .build()
                    .getBody());
        }
        var roles = String.join(",", userRoles.get().getRoles());
        var userName = userRoles.get().getUserName();
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                .body(Map.of("refresh-token", JwtTokenUtil.generateToken(email, roles, userName)))
                .build()
                .getBody());
    }
}
