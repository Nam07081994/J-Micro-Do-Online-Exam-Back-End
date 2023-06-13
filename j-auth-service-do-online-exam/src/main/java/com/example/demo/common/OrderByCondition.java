package com.example.demo.common;

import com.example.demo.constant.StringConstant;

public class OrderByCondition {
	public static String orderBy(int order_by) {
		return switch (order_by) {
			case 1 -> StringConstant.ORDER_STATEMENT + "endPoint" + " ASC ";
			case 2 -> StringConstant.ORDER_STATEMENT + "endPoint" + " DESC ";
			case 3 -> StringConstant.ORDER_STATEMENT + "createdAt" + " ASC ";
			case 4 -> StringConstant.ORDER_STATEMENT + "createAt" + " DESC ";
			case 5 -> StringConstant.ORDER_STATEMENT + "roleName" + " ASC ";
			case 6 -> StringConstant.ORDER_STATEMENT + "roleName" + " DESC ";
			case 7 -> StringConstant.ORDER_STATEMENT + "userName" + " ASC ";
			case 8 -> StringConstant.ORDER_STATEMENT + "userName" + " DESC ";
			case 9 -> StringConstant.ORDER_STATEMENT + "email" + " ASC ";
			case 10 -> StringConstant.ORDER_STATEMENT + "email" + " DESC ";
			default -> StringConstant.EMPTY_STRING;
		};
	}
}
