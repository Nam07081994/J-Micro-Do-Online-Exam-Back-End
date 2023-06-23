package com.jnotificationservicedoonlineexam.extra;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.List;

public class ListLongJsonType extends AbstractionJsonType<List<Long>> {
	public ListLongJsonType() {
		super(new TypeReference<>() {}, ArrayList::new);
	}
}
