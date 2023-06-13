package com.example.demo.common;

import static com.example.demo.constant.StringConstant.CREATED_AT_KEY;

import com.example.demo.command.CommonSearchCommand;
import com.example.demo.constant.StringConstant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueryDateCondition {
	private static String DEFAULT_DATE_FORMAT;

	@Value("${app.format.date}")
	public void setDefaultDatePattern(String pattern) {
		QueryDateCondition.DEFAULT_DATE_FORMAT = pattern;
	}

	public static boolean generate(
			CommonSearchCommand command, Map<String, QueryCondition> searchParams) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
		if (!command.getFrom_date().isEmpty() && !command.getTo_date().isEmpty()) {
			LocalDateTime from_date = LocalDateTime.parse(command.getFrom_date(), formatter);
			LocalDateTime to_date = LocalDateTime.parse(command.getTo_date(), formatter);
			if (to_date.isBefore(from_date)) {
				return true;
			}
		}

		if (!command.getFrom_date().isEmpty()) {
			searchParams.put(
					CREATED_AT_KEY,
					QueryCondition.builder()
							.value(LocalDateTime.parse(command.getFrom_date(), formatter))
							.operation(StringConstant.GREATER_THAN_OPERATION)
							.build());
		}

		if (!command.getTo_date().isEmpty()) {
			searchParams.put(
					CREATED_AT_KEY,
					QueryCondition.builder()
							.operation(StringConstant.LESS_THAN_OPERATOR)
							.value(LocalDateTime.parse(command.getTo_date(), formatter))
							.build());
		}
		return false;
	}
}
