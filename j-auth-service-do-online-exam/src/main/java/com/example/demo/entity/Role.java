package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tbl_role")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "created_by", nullable = false)
    @CreatedBy
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_by", nullable = false)
    @LastModifiedBy
    private String updatedBy;

    @Column(name = "updated_at")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
