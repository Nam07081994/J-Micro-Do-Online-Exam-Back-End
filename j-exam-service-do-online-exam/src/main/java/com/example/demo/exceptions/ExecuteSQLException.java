package com.example.demo.exceptions;

public class ExecuteSQLException extends Exception {
	public String sql;

	public ExecuteSQLException(String msg, String sql) {
		super(msg);
		this.sql = sql;
	}
}
