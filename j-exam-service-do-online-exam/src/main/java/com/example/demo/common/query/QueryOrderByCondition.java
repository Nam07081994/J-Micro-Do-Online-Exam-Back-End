package com.example.demo.common.query;

import static com.example.demo.constant.Constant.EMPTY_STRING;
import static com.example.demo.constant.SQLConstants.ORDER_STATEMENT;

public class QueryOrderByCondition {
	public static String orderBy(int order_by) {
		return switch (order_by) {
			case 1 -> ORDER_STATEMENT + "a.examName" + " ASC ";
			case 2 -> ORDER_STATEMENT + "a.examName" + " DESC ";
			case 3 -> ORDER_STATEMENT + "a.duration" + " ASC ";
			case 4 -> ORDER_STATEMENT + "a.duration" + " DESC ";
			case 5 -> ORDER_STATEMENT + "createdAt" + " ASC ";
			case 6 -> ORDER_STATEMENT + "createdAt" + " DESC ";
			case 7 -> ORDER_STATEMENT + "a.categoryName" + " ASC ";
			case 8 -> ORDER_STATEMENT + "a.categoryName" + " DESC ";
			case 9 -> ORDER_STATEMENT + "name" + " ASC ";
			case 10 -> ORDER_STATEMENT + "name" + " DESC ";
			default -> EMPTY_STRING;
		};
	}
}
