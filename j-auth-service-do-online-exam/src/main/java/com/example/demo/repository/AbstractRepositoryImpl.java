package com.example.demo.repository;

import static com.example.demo.constant.StringConstant.*;

import com.example.demo.common.query.QueryCondition;
import com.example.demo.constant.StringConstant;
import com.example.demo.exceptions.ExecuteSQLException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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

			var results = em.getResultList();
			var records = entityManager.createQuery(queryCount.toString(), Long.class);
			setQueryParam(searchParams, (TypedQuery<T>) records);

			var total = records.getSingleResult();
			int totalPages = (int) Math.ceil(total / (double) pageSize);

			return Map.of(
					StringConstant.DATA_KEY,
					results,
					StringConstant.PAGINATION_KEY,
					Map.of(StringConstant.PAGES_KEY, totalPages, StringConstant.PAGE_INDEX, pageIndex));
		} catch (Exception ex) {
			throw new ExecuteSQLException(ex.getMessage());
		}
	}

	private void setQueryParam(Map<String, QueryCondition> searchParams, TypedQuery<T> query)
			throws ExecuteSQLException {
		AtomicBoolean flag = new AtomicBoolean(false);
		searchParams.forEach(
				(s, s2) -> {
					switch (s2.getOperation()) {
						case StringConstant.LIKE_OPERATOR -> query.setParameter(
								s, PERCENT_OPERATOR + s2.getValue() + PERCENT_OPERATOR);
						case StringConstant.EQUAL_OPERATOR,
								StringConstant.GREATER_THAN_OPERATION,
								LESS_THAN_OPERATOR -> query.setParameter(s, s2.getValue());
						default -> flag.set(true);
					}
				});
		if (flag.get()) {
			throw new ExecuteSQLException(EMPTY_STRING);
		}
	}
}
