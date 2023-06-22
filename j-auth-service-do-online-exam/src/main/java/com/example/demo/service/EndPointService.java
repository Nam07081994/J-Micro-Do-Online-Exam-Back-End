package com.example.demo.service;

import static com.example.demo.constant.StringConstant.ENDPOINT_KEY;
import static com.example.demo.constant.TranslationCodeConstant.*;

import com.example.demo.command.CommonSearchCommand;
import com.example.demo.common.QueryCondition;
import com.example.demo.common.QueryDateCondition;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.constant.StringConstant;
import com.example.demo.dto.EndpointOptionDto;
import com.example.demo.entity.EndPoint;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.repository.AbstractRepositoryImpl;
import com.example.demo.repository.EndPointRepository;
import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EndPointService {

	private AbstractRepositoryImpl<EndPoint> abstractRepository;

	private EndPointRepository endPointRepository;

	private TranslationService translationService;

	public ResponseEntity<?> endPointService(CommonSearchCommand command, String name)
			throws ExecuteSQLException {
		Map<String, QueryCondition> searchParams = new HashMap<>();

		if (!name.isEmpty()) {
			searchParams.put(
					ENDPOINT_KEY,
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
						EndPoint.class);

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, result);
	}

	public List<String> getEndPointsByRole(List<Long> endPointIDs) {
		return endPointIDs.stream()
				.map(id -> endPointRepository.findById(id).get().getEndPoint())
				.toList();
	}

	public ResponseEntity<?> saveEndPoint(String endPointPath) {
		var endPointExist = endPointRepository.findByEndPoint(endPointPath);
		if (endPointExist.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(ENDPOINT_EXIST));
		}
		EndPoint endPoint = new EndPoint();
		endPoint.setEndPoint(endPointPath);
		endPointRepository.save(endPoint);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(SAVE_ENDPOINT_INFORMATION_SUCCESS));
	}

	public ResponseEntity<?> getEndpointsOption() {
		List<EndpointOptionDto> endpointOptionDtoList =
				endPointRepository.findAll().stream().map(EndpointOptionDto::new).toList();

		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(StringConstant.DATA_KEY, endpointOptionDtoList));
	}

	public ResponseEntity<?> editEndPoint(String endPointPath, Long id) {
		Optional<EndPoint> endPointOptionalByID = endPointRepository.findById(id);
		if (endPointOptionalByID.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(NOT_FOUND_ENDPOINT_INFORMATION));
		}

		Optional<EndPoint> endPointOptionalByName = endPointRepository.findByEndPoint(endPointPath);
		if (!endPointOptionalByID.get().getEndPoint().equals(endPointPath)
				&& endPointOptionalByName.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(ENDPOINT_EXIST));
		}
		endPointOptionalByID.get().setEndPoint(endPointPath);
		endPointRepository.save(endPointOptionalByID.get());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(EDIT_ENDPOINT_INFORMATION_SUCCESS));
	}

	public ResponseEntity<?> deleteEndPoint(Long id) {
		Optional<EndPoint> endPointOptionalByID = endPointRepository.findById(id);
		if (endPointOptionalByID.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(NOT_FOUND_ENDPOINT_INFORMATION));
		}
		endPointRepository.delete(endPointOptionalByID.get());

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(DELETE_ENDPOINT_INFORMATION_SUCCESS));
	}

	public boolean checkEndpointExist(Long id) {
		return endPointRepository.findById(id).isPresent();
	}

	public ResponseEntity<?> getPublicEndPoint(String type){
		var publicEndPoints = endPointRepository.findByEndPoint(type);
		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(StringConstant.DATA_KEY, publicEndPoints));
	}
}
