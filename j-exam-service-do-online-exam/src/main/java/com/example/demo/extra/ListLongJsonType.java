package com.example.demo.extra;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.type.TypeReference;

public class ListLongJsonType extends AbstractionJsonType<List<Long>>{
    public ListLongJsonType(){
        super(new TypeReference<>() {}, ArrayList::new);
    }
}
