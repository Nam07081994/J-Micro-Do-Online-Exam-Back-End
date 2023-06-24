package com.example.demo.common.query;

import static com.example.demo.constant.SQLConstants.*;

import com.example.demo.command.QuerySearchCommand;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

public class QueryDateCondition {
	private static String DEFAULT_DATE_FORMAT;

	@Value("${app.format.date}")
	public void setDefaultDatePattern(String pattern) {
		QueryDateCondition.DEFAULT_DATE_FORMAT = pattern;
	}

	public static boolean generate(
			QuerySearchCommand command, Map<String, QueryCondition> searchParams) {
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
							.operation(GREATER_THAN_OPERATION)
							.build());
		}

		if (!command.getTo_date().isEmpty()) {
			searchParams.put(
					CREATED_AT_KEY,
					QueryCondition.builder()
							.operation(LESS_THAN_OPERATOR)
							.value(LocalDateTime.parse(command.getTo_date(), formatter))
							.build());
		}
		return false;
	}
}
