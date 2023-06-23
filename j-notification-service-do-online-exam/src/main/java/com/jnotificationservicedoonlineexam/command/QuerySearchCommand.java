package com.jnotificationservicedoonlineexam.command;

import com.jnotificationservicedoonlineexam.common.annotations.DateFormatCheck;
import com.jnotificationservicedoonlineexam.common.query.QueryOrderByCondition;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuerySearchCommand {
	private static final int DEFAULT_PAGE_INDEX = 1;
	private static final int DEFAULT_PAGE_SIZE = 7;
	private static final int DEFAULT_MAX_PAGE_SIZE = 30;

	@DateFormatCheck private String from_date;

	@DateFormatCheck private String to_date;

	private int page_index;

	private int page_size;

	private String order_by;

	public static QuerySearchCommand from(
			String from_date, String to_date, int page_index, int page_size, int order_by) {
		return QuerySearchCommand.builder()
				.from_date(from_date)
				.to_date(to_date)
				.order_by(QueryOrderByCondition.orderBy(order_by))
				.page_index(page_index < 0 ? DEFAULT_PAGE_INDEX : page_index)
				.page_size(page_size < 0 ? DEFAULT_PAGE_SIZE : (Math.min(page_size, DEFAULT_MAX_PAGE_SIZE)))
				.build();
	}
}
