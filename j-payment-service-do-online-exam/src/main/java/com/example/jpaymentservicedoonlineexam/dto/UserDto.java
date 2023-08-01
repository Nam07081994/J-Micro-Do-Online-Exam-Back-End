package com.example.jpaymentservicedoonlineexam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private String createAt;
    private String phone;
    private String address;
    private String birthday;
    private String image;
    private List<String> roles;
}
