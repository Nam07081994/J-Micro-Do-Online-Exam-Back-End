package com.example.demo.service;

import static com.example.demo.constant.StringConstant.DATA_KEY;
import static com.example.demo.constant.StringConstant.ROLE_NAME_KEY;
import static com.example.demo.constant.TranslationCodeConstant.FROM_DATE_TO_DATE_INVALID;

import com.example.demo.command.CommonSearchCommand;
import com.example.demo.command.RoleCommand;
import com.example.demo.common.query.QueryCondition;
import com.example.demo.common.query.QueryDateCondition;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.constant.StringConstant;
import com.example.demo.constant.TranslationCodeConstant;
import com.example.demo.dto.RoleDto;
import com.example.demo.entity.Role;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.exceptions.InvalidDateFormatException;
import com.example.demo.repository.AbstractRepositoryImpl;
import com.example.demo.repository.RoleRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@EnableTransactionManagement
public class RoleService {

	private RoleRepository roleRepository;

	private EndPointService endPointService;

	private TranslationService translationService;

	private AbstractRepositoryImpl<Role> abstractRepository;

	public ResponseEntity<?> getRoles(CommonSearchCommand command, String name)
			throws ExecuteSQLException, InvalidDateFormatException {
		Map<String, QueryCondition> searchParams = new HashMap<>();

		if (!StringUtils.isEmpty(name)) {
			searchParams.put(
					ROLE_NAME_KEY,
					QueryCondition.builder().operation(StringConstant.LIKE_OPERATOR).value(name).build());
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
						Role.class);

		List<Role> roles = (List<Role>) result.get(DATA_KEY);
		result.put(DATA_KEY, roles.stream().map(RoleDto::new).collect(Collectors.toList()));

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, result);
	}

	@Transactional
	public ResponseEntity<?> makeRole(RoleCommand command) {
		Optional<Role> roleOpt = roleRepository.findByRoleName(command.getName());
		if (roleOpt.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(TranslationCodeConstant.ROLE_NAME_EXIST));
		}

		for (Long endPointID : command.getEndPoint()) {
			if (!validateEndpoint(endPointID)) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST,
						translationService.getTranslation(
								TranslationCodeConstant.NOT_FOUND_ENDPOINT_INFORMATION));
			}
		}
		Role newRole =
				Role.builder().roleName(command.getName()).endPoint(command.getEndPoint()).build();
		roleRepository.save(newRole);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK,
				translationService.getTranslation(TranslationCodeConstant.SAVE_ROLE_INFORMATION_SUCCESS));
	}

	@Transactional
	public ResponseEntity<?> editRole(RoleCommand command, Long id) {
		Optional<Role> roleOptById = roleRepository.findById(id);
		if (roleOptById.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(TranslationCodeConstant.NOT_FOUND_ROLE_INFORMATION));
		}

		Optional<Role> roleOptByName = roleRepository.findByRoleName(command.getName());
		if (!roleOptById.get().getRoleName().equals(command.getName()) && roleOptByName.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(TranslationCodeConstant.ROLE_NAME_EXIST));
		}

		for (Long endPointID : command.getEndPoint()) {
			if (!validateEndpoint(endPointID)) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST,
						translationService.getTranslation(
								TranslationCodeConstant.NOT_FOUND_ENDPOINT_INFORMATION));
			}
		}

		roleOptById.get().setRoleName(command.getName());
		roleOptById.get().setEndPoint(command.getEndPoint());
		roleRepository.save(roleOptById.get());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK,
				translationService.getTranslation(TranslationCodeConstant.EDIT_ROLE_INFORMATION_SUCCESS));
	}

	private boolean validateEndpoint(Long id) {
		return endPointService.checkEndpointExist(id);
	}

	public ResponseEntity<?> getRoleByName(String name) {
		Optional<Role> roleOpt = roleRepository.findByRoleName(name);
		if (roleOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(TranslationCodeConstant.NOT_FOUND_ROLE_INFORMATION));
		}

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(StringConstant.DATA_KEY, new RoleDto(roleOpt.get())));
	}

	public ResponseEntity<?> getEndpointsByRole(Long id) {
		Optional<Role> roleOpt = roleRepository.findById(id);
		if (roleOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(TranslationCodeConstant.NOT_FOUND_ROLE_INFORMATION));
		}

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK,
				Map.of(
						StringConstant.DATA_KEY,
						endPointService.getEndPointsByRole(roleOpt.get().getEndPoint())));
	}
}
