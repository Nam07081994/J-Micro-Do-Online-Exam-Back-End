package com.example.demo.common.response;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class HTTPResponseObject {
    private String message;

    private HashMap<String, Object> body;

}
