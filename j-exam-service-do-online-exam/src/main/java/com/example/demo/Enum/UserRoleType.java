package com.example.demo.Enum;

import lombok.Getter;

@Getter
public enum UserRoleType {
    USER(0),
    USER_PREMIUM(100000),
    ;

    private final Integer price;

    UserRoleType(Integer price) {
        this.price = price;
    }
}
