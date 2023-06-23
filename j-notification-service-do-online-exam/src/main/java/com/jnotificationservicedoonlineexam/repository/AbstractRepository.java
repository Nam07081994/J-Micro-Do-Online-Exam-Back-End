package com.jnotificationservicedoonlineexam.repository;

import com.jnotificationservicedoonlineexam.common.query.QueryCondition;
import com.jnotificationservicedoonlineexam.exceptions.ExecuteSQLException;
import java.util.Map;

public interface AbstractRepository<T> {
	Map<String, Object> search(
			Map<String, QueryCondition> searchParams,
			Map<String, QueryCondition> orParams,
			String orderBy,
			int pageSize,
			int pageIndex,
			Class<T> clazz)
			throws ExecuteSQLException;
}
