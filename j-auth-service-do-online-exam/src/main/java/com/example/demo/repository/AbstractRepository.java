package com.example.demo.repository;

import com.example.demo.common.QueryCondition;
import com.example.demo.exceptions.ExecuteSQLException;
import java.util.Map;

public interface AbstractRepository<T> {
	Map<String, Object> search(
			Map<String, QueryCondition> searchParams,
			String orderBy,
			int pageSize,
			int pageIndex,
			Class<T> clazz)
			throws ExecuteSQLException;
}
