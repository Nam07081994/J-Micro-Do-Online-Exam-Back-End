package com.jnotificationservicedoonlineexam.common.query;

import static com.jnotificationservicedoonlineexam.constants.Constants.EMPTY_STRING;
import static com.jnotificationservicedoonlineexam.constants.SQLConstants.ORDER_STATEMENT;

public class QueryOrderByCondition {
	public static String orderBy(int order_by) {
		return switch (order_by) {
			case 1 -> ORDER_STATEMENT + "topicName" + " ASC ";
			case 2 -> ORDER_STATEMENT + "topicName" + " DESC ";
			case 3 -> ORDER_STATEMENT + "createdAt" + " ASC ";
			case 4 -> ORDER_STATEMENT + "createdAt" + " DESC ";
			default -> EMPTY_STRING;
		};
	}
}
