package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tbl_accounts_exam")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountExam {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "contest_id", nullable = false)
	private Long contestID;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "role_name", nullable = false)
	private String role;

	@Column(name = "start_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startAt;

	@Column(name = "end_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endAt;

	@Column(name = "created_by")
	@CreatedBy
	private String createdBy;

	@Column(name = "created_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@CreatedDate
	private LocalDateTime createdAt;

	@Column(name = "updated_by")
	@LastModifiedBy
	private String updatedBy;

	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@LastModifiedDate
	private LocalDateTime updatedAt;
}
