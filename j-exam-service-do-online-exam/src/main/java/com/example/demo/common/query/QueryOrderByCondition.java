package com.example.demo.common.query;

import static com.example.demo.constant.Constant.EMPTY_STRING;
import static com.example.demo.constant.SQLConstants.ORDER_STATEMENT;

public class QueryOrderByCondition {
	public static String orderBy(int order_by) {
		return switch (order_by) {
			case 1 -> ORDER_STATEMENT + "examName" + " ASC ";
			case 2 -> ORDER_STATEMENT + "examName" + " DESC ";
			case 3 -> ORDER_STATEMENT + "duration" + " ASC ";
			case 4 -> ORDER_STATEMENT + "duration" + " DESC ";
			case 5 -> ORDER_STATEMENT + "createdAt" + " ASC ";
			case 6 -> ORDER_STATEMENT + "createdAt" + " DESC ";
			case 7 -> ORDER_STATEMENT + "categoryName" + " ASC ";
			case 8 -> ORDER_STATEMENT + "categoryName" + " DESC ";
			default -> EMPTY_STRING;
		};
	}
}
