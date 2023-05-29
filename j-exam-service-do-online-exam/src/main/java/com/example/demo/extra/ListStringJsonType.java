package com.example.demo.extra;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

public class ListStringJsonType extends AbstractionJsonType<List<String>>{
    public ListStringJsonType(){
        super(new TypeReference<>() {}, ArrayList::new);
    }
}
