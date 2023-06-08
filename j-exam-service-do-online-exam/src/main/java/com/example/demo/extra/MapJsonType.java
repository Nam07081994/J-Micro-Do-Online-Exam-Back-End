package com.example.demo.extra;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

public class MapJsonType extends AbstractionJsonType<Map<String, String>>{
    public MapJsonType(){
        super(new TypeReference<>() {}, HashMap::new);
    }
}
