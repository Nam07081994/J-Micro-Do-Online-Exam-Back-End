package com.example.demo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class AbstractRepositoryImpl<T> implements AbstractRepository<T> {

	@PersistenceContext EntityManager entityManager;

	@Override
	public List<T> search(Map<String, String> searchParams, Class<T> clazz) {
		StringBuilder query = new StringBuilder("SELECT c FROM " + clazz.getName() + " c ");

		if (!searchParams.isEmpty()) {
			query.append(" WHERE ");
			String search =
					searchParams.keySet().stream()
							.map(s -> "c." + s + " = :" + s)
							.collect(Collectors.joining(" AND "));
			query.append(search);
			System.out.println(query);
		}

		var em = entityManager.createQuery(query.toString(), clazz);
		searchParams.forEach(em::setParameter);

		return em.getResultList();
	}
}
