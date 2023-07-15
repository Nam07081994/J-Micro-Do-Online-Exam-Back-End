package com.example.demo.repository;

import static com.example.demo.constant.Constant.*;
import static com.example.demo.constant.SQLConstants.*;

import com.example.demo.common.query.QueryCondition;
import com.example.demo.exceptions.ExecuteSQLException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
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
								.map(
										s -> {
											if (searchParams.get(s).getOperation().equals(BETWEEN_OPERATOR)) {
												return "c."
														+ s
														+ SPACE_STRING
														+ searchParams.get(s).getOperation()
														+ " :"
														+ s
														+ " AND :secondValue";
											} else {
												return "c."
														+ s
														+ SPACE_STRING
														+ searchParams.get(s).getOperation()
														+ " :"
														+ s;
											}
										})
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

			return new HashMap<>(
					Map.of(
							DATA_KEY,
							results,
							PAGINATION_KEY,
							Map.of(PAGES_KEY, totalPages, PAGE_INDEX, pageIndex, TOTAL_RECORDS_KEY, total)));
		} catch (Exception ex) {
			// TODO: add sql error
			throw new ExecuteSQLException(ex.getMessage(), "");
		}
	}

	@Override
	public Map<String, Object> searchWithUnion(
			Map<String, QueryCondition> searchParams,
			List<String> keyParams,
			String orderBy,
			int pageSize,
			int pageIndex,
			Class<T> clazz)
			throws ExecuteSQLException {
		try {
			StringBuilder queryStatement =
					new StringBuilder("SELECT c FROM " + clazz.getSimpleName() + " c ");
			StringBuilder unionStatement =
					new StringBuilder(" UNION ALL SELECT a FROM " + clazz.getSimpleName() + " a ");
			StringBuilder defaultUnionStatement = new StringBuilder(" AND a.ownerId <> :ownerId");

			if (!searchParams.isEmpty()) {
				queryStatement.append(WHERE_STATEMENT);
				String search =
						searchParams.keySet().stream()
								.map(
										s -> {
											if (clazz.getSimpleName().equals("Exam")) {
												if (s.equals(EXAM_TYPE_SEARCH_KEY)) {
													return "1 = 1";
												} else if (searchParams.get(s).getOperation().equals(BETWEEN_OPERATOR)) {
													return "c."
															+ s
															+ SPACE_STRING
															+ searchParams.get(s).getOperation()
															+ " :"
															+ s
															+ " AND :secondValue";
												} else {
													return "c."
															+ s
															+ SPACE_STRING
															+ searchParams.get(s).getOperation()
															+ " :"
															+ s;
												}
											} else {
												if (searchParams.get(s).getOperation().equals(BETWEEN_OPERATOR)) {
													return "c."
															+ s
															+ SPACE_STRING
															+ searchParams.get(s).getOperation()
															+ " :"
															+ s
															+ " AND :secondValue";
												} else {
													return "c."
															+ s
															+ SPACE_STRING
															+ searchParams.get(s).getOperation()
															+ " :"
															+ s;
												}
											}
										})
								.collect(Collectors.joining(AND_STATEMENT));
				queryStatement.append(search);

				if (clazz.getSimpleName().equals("Exam")) {
					unionStatement.append(WHERE_STATEMENT);
					String unionSearch =
							searchParams.keySet().stream()
									.map(
											s -> {
												if (s.equals(EXAM_OWNER_ID_SEARCH_KEY)) {
													return "1 = 1";
												} else if (searchParams.get(s).getOperation().equals(BETWEEN_OPERATOR)) {
													return "a."
															+ s
															+ SPACE_STRING
															+ searchParams.get(s).getOperation()
															+ " :"
															+ s
															+ " AND :secondValue";
												} else {
													return "a."
															+ s
															+ SPACE_STRING
															+ searchParams.get(s).getOperation()
															+ " :"
															+ s;
												}
											})
									.collect(Collectors.joining(AND_STATEMENT));
					queryStatement.append(unionStatement).append(unionSearch).append(defaultUnionStatement);
				}
			}

			// Append order by statement
			if (!orderBy.isEmpty() && !clazz.getSimpleName().equals("Exam")) {
				queryStatement.append(orderBy);
			}

			int startIndex = (pageIndex - 1) * pageSize;

			var em =
					entityManager
							.createQuery(queryStatement.toString(), clazz)
							.setFirstResult(startIndex)
							.setMaxResults(pageSize);
			setQueryParam(searchParams, em);

			var results = em.getResultList();
			var records = entityManager.createQuery(queryStatement.toString(), clazz);
			setQueryParam(searchParams, records);

			var total = records.getResultList();
			int totalPages = (int) Math.ceil(total.size() / (double) pageSize);

			return new HashMap<>(
					Map.of(
							DATA_KEY,
							results,
							PAGINATION_KEY,
							Map.of(
									PAGES_KEY, totalPages, PAGE_INDEX, pageIndex, TOTAL_RECORDS_KEY, total.size())));
		} catch (Exception ex) {
			throw new ExecuteSQLException(ex.getMessage(), "");
		}
	}

	private void setQueryParam(Map<String, QueryCondition> searchParams, TypedQuery<T> query)
			throws ExecuteSQLException {
		AtomicBoolean flag = new AtomicBoolean(false);
		searchParams.forEach(
				(s, s2) -> {
					switch (s2.getOperation()) {
						case BETWEEN_OPERATOR -> {
							query.setParameter(s, s2.getValue());
							query.setParameter("secondValue", s2.getValue2());
						}
						case LIKE_OPERATOR, NOT_LIKE_OPERATOR -> query.setParameter(
								s, PERCENT_OPERATOR + s2.getValue() + PERCENT_OPERATOR);
						case EQUAL_OPERATOR,
								NOT_EQUAL_OPERATOR,
								GREATER_THAN_OPERATION,
								LESS_THAN_OPERATOR,
								IN_OPERATOR -> query.setParameter(s, s2.getValue());
						default -> flag.set(true);
					}
				});
		if (flag.get()) {
			// TODO:
			throw new ExecuteSQLException(EMPTY_STRING, "");
		}
	}
}
