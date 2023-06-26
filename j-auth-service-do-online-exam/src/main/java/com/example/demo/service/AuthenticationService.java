package com.example.demo.service;

import static com.example.demo.constant.StringConstant.*;
import static com.example.demo.constant.TranslationCodeConstant.*;

import com.example.demo.command.CommonSearchCommand;
import com.example.demo.command.LoginCommand;
import com.example.demo.command.RegisterCommand;
import com.example.demo.command.UpdateUserInfoCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.query.QueryCondition;
import com.example.demo.common.query.QueryDateCondition;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.config.jpa.JpaConfig;
import com.example.demo.constant.StringConstant;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.exceptions.InvalidDateFormatException;
import com.example.demo.repository.AbstractRepositoryImpl;
import com.example.demo.repository.EndPointRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Claims;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
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

@Service
public class AuthenticationService {

	@Value("${app.default-user-thumbnail}")
	private String DEFAULT_USER_THUMBNAIL_URI;

	@Value("${app.file-service-endpoint}")
	private String UPDATE_THUMBNAIL_URI;

	@Value("${app.upload-rule.user.contest}")
	private Integer A_NUMBER_UPLOAD_CONTEST;

	@Value("${app.upload-rule.user.exam}")
	private Integer A_NUMBER_UPLOAD_EXAM;

	@Autowired private UserRepository userRepository;

	@Autowired private RoleRepository roleRepository;

	@Autowired private EndPointRepository endPointRepository;

	@Autowired private PasswordEncoder passwordEncoder;

	@Autowired private RestTemplate restTemplate;

	@Autowired private TranslationService translationService;

	@Autowired private AuthenticationManager authenticationManager;

	@Autowired private AbstractRepositoryImpl<User> abstractRepository;

	public ResponseEntity<?> getUsers(
			CommonSearchCommand command, String email, String username, String phone)
			throws ExecuteSQLException, InvalidDateFormatException {
		Map<String, QueryCondition> searchParams = new HashMap<>();

		if (!StringUtils.isEmpty(email)) {
			searchParams.put(
					EMAIL_KEY, QueryCondition.builder().operation(LIKE_OPERATOR).value(email).build());
		}

		if (!StringUtils.isEmpty(username)) {
			searchParams.put(
					USERNAME_KEY, QueryCondition.builder().operation(LIKE_OPERATOR).value(username).build());
		}

		if (!StringUtils.isEmpty(phone)) {
			searchParams.put(
					PHONE_KEY, QueryCondition.builder().operation(LIKE_OPERATOR).value(phone).build());
		}

		if (QueryDateCondition.generate(command, searchParams))
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(FROM_DATE_TO_DATE_INVALID));

		var result =
				abstractRepository.search(
						searchParams,
						command.getOrder_by(),
						command.getPage_size(),
						command.getPage_index(),
						User.class);

		List<User> users = (List<User>) result.get(StringConstant.DATA_KEY);
		result.put(
				StringConstant.DATA_KEY,
				users.stream()
						.map(
								u -> {
									String roles = getUserRolesString(u.getRoles());
									List<String> rolesArr =
											new ArrayList<>(Arrays.asList(roles.split(COMMA_STRING_CHARACTER)));
									return new UserDto(u, rolesArr);
								})
						.collect(Collectors.toList()));

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, result);
	}

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

		Map<Integer, Integer> uploadNumber =
				Map.of(EXAM_UPLOAD_KEY, A_NUMBER_UPLOAD_EXAM, CONTEST_UPLOAD_KEY, A_NUMBER_UPLOAD_CONTEST);

		var user =
				User.builder()
						.userName(command.getUserName())
						.email(command.getEmail())
						.uploadNumber(uploadNumber)
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
			var userID = String.valueOf(userOpt.get().getId());
			String roleResult = getUserRolesString(userOpt.get().getRoles());

			return GenerateResponseHelper.generateDataResponse(
					HttpStatus.OK,
					Map.of(
							StringConstant.ACCESS_TOKEN_KEY,
							JwtTokenUtil.generateToken(
									userOpt.get().getUserName(), command.getEmail(), roleResult, userID)));
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
			body.add(StringConstant.OLD_IMAGE_PATH_KEY, userOptional.get().getThumbnail());
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
			String roles = getUserRolesString(userOptional.get().getRoles());
			List<String> rolesArr = new ArrayList<>(Arrays.asList(roles.split(COMMA_STRING_CHARACTER)));

			return GenerateResponseHelper.generateDataResponse(
					HttpStatus.OK,
					Map.of(StringConstant.DATA_KEY, new UserDto(userOptional.get(), rolesArr)));
		} catch (Exception ex) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(ERROR_GET_USER_INFOR));
		}
	}

	public ResponseEntity<?> refreshToken(String token) {
		var tokenExpired = JwtTokenUtil.isTokenExpired(token.substring(7));
		if (!tokenExpired) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(INVALID_TOKEN_INFORMATION));
		}
		var email = (String) JwtTokenUtil.getUserInfoFromToken(token, Claims::getId);
		var userRoles = userRepository.findByEmail(email);
		if (userRoles.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_USER_INFORMATION));
		}

		var userID = String.valueOf(userRoles.get().getId());
		String roleResult = getUserRolesString(userRoles.get().getRoles());

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK,
				Map.of(
						REFRESH_TOKEN_KEY,
						JwtTokenUtil.generateToken(userRoles.get().getUserName(), email, roleResult, userID)));
	}

	private String getUserRolesString(List<Long> roleIDs) {
		StringBuilder roles = new StringBuilder(StringConstant.EMPTY_STRING);
		for (Long roleID : roleIDs) {
			roles
					.append(roleRepository.findById(roleID).get().getRoleName())
					.append(COMMA_STRING_CHARACTER);
		}

		return roles.substring(0, roles.length() - 1);
	}

	public ResponseEntity<?> checkUserAction(String token, int flag) {
		Long userID = (Long) JwtTokenUtil.getUserInfoFromToken(token, Claims::getAudience);
		Optional<User> userOpt = userRepository.findById(userID);
		if (userOpt.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		Map<Integer, Integer> userUploadNumber = userOpt.get().getUploadNumber();
		if (userUploadNumber.get(flag) <= 0) {
			return ResponseEntity.badRequest().build();
		}

		var newValue = userUploadNumber.get(flag) - 1;
		userUploadNumber.put(flag, newValue);
		userOpt.get().setUploadNumber(userUploadNumber);
		userRepository.save(userOpt.get());

		return ResponseEntity.ok().build();
	}

	public ResponseEntity<?> updateUserInfo(String token, UpdateUserInfoCommand command) {
		Long userID = (Long) JwtTokenUtil.getUserInfoFromToken(token, Claims::getAudience);
		Optional<User> userOpt = userRepository.findById(userID);
		Optional<User> userOptByEmail = userRepository.findByEmail(command.getEmail());
		if (userOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_USER_INFORMATION));
		}

		if (!userOpt.get().getEmail().equals(command.getEmail()) && userOptByEmail.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_EMAIL_EXIST));
		}

		if (!StringUtils.isEmpty(command.getBirthday())) {
			userOpt.get().setBirthday(command.getBirthday());
		}

		if (!StringUtils.isEmpty(command.getPhone())) {
			userOpt.get().setPhone(command.getPhone());
		}

		if (!StringUtils.isEmpty(command.getAddress())) {
			userOpt.get().setAddress(command.getAddress());
		}

		userOpt.get().setUserName(command.getUserName());
		userOpt.get().setEmail(command.getEmail());

		userRepository.save(userOpt.get());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(UPDATE_USER_INFO_SUCCESS));
	}
}
