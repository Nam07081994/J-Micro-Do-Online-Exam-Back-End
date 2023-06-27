package com.example.demo.common.query;

import static com.example.demo.constant.SQLConstants.*;

import com.example.demo.command.QuerySearchCommand;
import com.example.demo.exceptions.InvalidDateFormatException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

public class QueryDateCondition {
	private static String DEFAULT_DATE_FORMAT;

	@Value("${app.format.date}")
	public void setDefaultDatePattern(String pattern) {
		QueryDateCondition.DEFAULT_DATE_FORMAT = pattern;
	}

	public static boolean generate(
			QuerySearchCommand command, Map<String, QueryCondition> searchParams)
			throws InvalidDateFormatException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
		try {
			if (!StringUtils.isEmpty(command.getFrom_date())
					&& !StringUtils.isEmpty(command.getTo_date())) {
				LocalDateTime from_date = LocalDateTime.parse(command.getFrom_date(), formatter);
				LocalDateTime to_date = LocalDateTime.parse(command.getTo_date(), formatter);
				if (to_date.isBefore(from_date)) {
					return true;
				}
			}

			if (!StringUtils.isEmpty(command.getFrom_date())) {
				searchParams.put(
						CREATED_AT_KEY,
						QueryCondition.builder()
								.value(LocalDateTime.parse(command.getFrom_date(), formatter))
								.operation(GREATER_THAN_OPERATION)
								.build());
			}

			if (!StringUtils.isEmpty(command.getTo_date())) {
				searchParams.put(
						CREATED_AT_KEY,
						QueryCondition.builder()
								.operation(LESS_THAN_OPERATOR)
								.value(LocalDateTime.parse(command.getTo_date(), formatter))
								.build());
			}
		} catch (DateTimeParseException ex) {
			throw new InvalidDateFormatException(
					"Invalid date format! Enter date follow format yyyy-mm-dd");
		}

		return false;
	}
}