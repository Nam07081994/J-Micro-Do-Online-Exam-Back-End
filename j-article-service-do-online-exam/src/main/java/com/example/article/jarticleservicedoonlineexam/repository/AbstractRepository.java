package com.example.article.jarticleservicedoonlineexam.repository;

import com.example.article.jarticleservicedoonlineexam.common.query.QueryCondition;
import com.example.article.jarticleservicedoonlineexam.exceptions.ExecuteSQLException;
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
