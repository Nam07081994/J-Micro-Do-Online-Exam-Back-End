package com.jnotificationservicedoonlineexam.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jnotificationservicedoonlineexam.extra.ListLongJsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.Type;
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
@Table(name = "tbl_topics")
@EntityListeners(AuditingEntityListener.class)
public class Topic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty(message = "Topic name is mandatory")
	private String topicName;

	@Type(ListLongJsonType.class)
	@Column(columnDefinition = "jsonb")
	private List<Long> subscribersID;

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
