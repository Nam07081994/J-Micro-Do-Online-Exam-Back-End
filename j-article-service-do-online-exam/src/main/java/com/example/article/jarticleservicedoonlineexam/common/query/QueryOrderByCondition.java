package com.example.article.jarticleservicedoonlineexam.common.query;

import static com.example.article.jarticleservicedoonlineexam.constants.Constants.EMPTY_STRING;
import static com.example.article.jarticleservicedoonlineexam.constants.SQLConstants.ORDER_STATEMENT;

public class QueryOrderByCondition {
	public static String orderBy(int order_by) {
		return switch (order_by) {
			case 1 -> ORDER_STATEMENT + "title" + " ASC ";
			case 2 -> ORDER_STATEMENT + "title" + " DESC ";
			case 3 -> ORDER_STATEMENT + "createdAt" + " ASC ";
			case 4 -> ORDER_STATEMENT + "createdAt" + " DESC ";
			default -> EMPTY_STRING;
		};
	}
}
