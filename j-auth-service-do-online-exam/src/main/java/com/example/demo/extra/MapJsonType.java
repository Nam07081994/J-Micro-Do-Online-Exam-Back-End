package com.example.demo.extra;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.Map;

public class MapJsonType extends AbstractionJsonType<Map<Integer, Integer>> {
	public MapJsonType() {
		super(new TypeReference<>() {}, HashMap::new);
	}
}
