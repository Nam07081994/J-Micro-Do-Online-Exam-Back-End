package com.jnotificationservicedoonlineexam.entity;

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
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_notifications")
@EntityListeners(AuditingEntityListener.class)
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private String sender;

	private Boolean isChecked;

	private LocalDateTime sentAt;

	private String notificationTitle;

	private String notificationContent;

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
