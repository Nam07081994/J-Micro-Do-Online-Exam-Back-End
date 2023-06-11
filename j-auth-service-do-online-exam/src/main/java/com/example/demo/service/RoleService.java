package com.example.demo.service;

import com.example.demo.command.RoleCommand;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.constant.StringConstant;
import com.example.demo.constant.TranslationCodeConstant;
import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
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

	public ResponseEntity<?> getRoles() {
		return null;
	}

	@Transactional
	public ResponseEntity<?> makeRole(RoleCommand command) {
		Optional<Role> roleOpt = roleRepository.findByRoleName(command.getName());
		if (roleOpt.isEmpty()) {
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

		// TODO convert role -> roleDto
		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(StringConstant.DATA_KEY, roleOpt.get()));
	}
}
