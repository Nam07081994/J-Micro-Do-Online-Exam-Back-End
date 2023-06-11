package com.example.demo.repository;

import java.util.List;
import java.util.Map;

public interface AbstractRepository<T> {

	List<T> search(Map<String, String> searchParams, Class<T> clazz);
}
