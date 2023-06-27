package com.example.demo.repository;

import static com.example.demo.constant.Constant.*;
import static com.example.demo.constant.SQLConstants.*;

import com.example.demo.common.query.QueryCondition;
import com.example.demo.exceptions.ExecuteSQLException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class AbstractRepositoryImpl<T> implements AbstractRepository<T> {

	@PersistenceContext EntityManager entityManager;

	@Override
	public Map<String, Object> search(
			Map<String, QueryCondition> searchParams,
			Map<String, QueryCondition> orParams,
			String orderBy,
			int pageSize,
			int pageIndex,
			Class<T> clazz)
			throws ExecuteSQLException {
		try {
			StringBuilder query = new StringBuilder("SELECT c FROM " + clazz.getSimpleName() + " c ");
			StringBuilder queryCount =
					new StringBuilder("SELECT COUNT(c) FROM " + clazz.getSimpleName() + " c ");

			// Append where statement
			if (!searchParams.isEmpty()) {
				query.append(WHERE_STATEMENT);
				queryCount.append(WHERE_STATEMENT);
				String search =
						searchParams.keySet().stream()
								.map(s -> "c." + s + SPACE_STRING + searchParams.get(s).getOperation() + " :" + s)
								.collect(Collectors.joining(AND_STATEMENT));
				query.append(search);
				queryCount.append(search);
			}

			if (!orParams.isEmpty()) {
				query.append(ORDER_STATEMENT);
				queryCount.append(ORDER_STATEMENT);
				String orStatement =
						orParams.keySet().stream()
								.map(s -> "c. " + s + " " + orParams.get(s).getOperation() + " :" + s)
								.collect(Collectors.joining(AND_STATEMENT));
				query.append(orStatement);
				queryCount.append(orStatement);
			}

			// Append order by statement
			if (!orderBy.isEmpty()) {
				query.append(orderBy);
			}
			int startIndex = (pageIndex - 1) * pageSize;

			var em =
					entityManager
							.createQuery(query.toString(), clazz)
							.setFirstResult(startIndex)
							.setMaxResults(pageSize);
			setQueryParam(searchParams, em);
			setQueryParam(orParams, em);

			var results = em.getResultList();
			var records = entityManager.createQuery(queryCount.toString(), Long.class);
			setQueryParam(searchParams, (TypedQuery<T>) records);
			setQueryParam(orParams, (TypedQuery<T>) records);

			var total = records.getSingleResult();
			int totalPages = (int) Math.ceil(total / (double) pageSize);

			return new HashMap<>(Map.of(
					DATA_KEY, results, PAGINATION_KEY, Map.of(PAGES_KEY, totalPages, PAGE_INDEX, pageIndex)));
		} catch (Exception ex) {
			// TODO: add sql error
			throw new ExecuteSQLException(ex.getMessage(), "");
		}
	}

	private void setQueryParam(Map<String, QueryCondition> searchParams, TypedQuery<T> query)
			throws ExecuteSQLException {
		AtomicBoolean flag = new AtomicBoolean(false);
		searchParams.forEach(
				(s, s2) -> {
					switch (s2.getOperation()) {
						case LIKE_OPERATOR -> query.setParameter(
								s, PERCENT_OPERATOR + s2.getValue() + PERCENT_OPERATOR);
						case EQUAL_OPERATOR, GREATER_THAN_OPERATION, LESS_THAN_OPERATOR, IN_OPERATOR -> query
								.setParameter(s, s2.getValue());
						default -> flag.set(true);
					}
				});
		if (flag.get()) {
			// TODO:
			throw new ExecuteSQLException(EMPTY_STRING, "");
		}
	}
}
