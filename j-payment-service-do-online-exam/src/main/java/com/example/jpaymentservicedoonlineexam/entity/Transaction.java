package com.example.jpaymentservicedoonlineexam.entity;

import com.example.jpaymentservicedoonlineexam.Enum.PaymentStatusType;
import com.example.jpaymentservicedoonlineexam.Enum.RecurringPaymentType;
import com.example.jpaymentservicedoonlineexam.Enum.TransactionStatusType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_transactions")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    private String transactionNo;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long amount;
    @Column(nullable = false)
    private String bankCode;
    @Column(nullable = false)
    private String bankTranNo;
    @Column(nullable = false)
    private String cardType;
    @Column(nullable = false)
    private String orderInfo;
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate payDate;
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiredDate;

    @Column(nullable = false)
    private PaymentStatusType responseCode;
    @Column(nullable = false)
    private TransactionStatusType transactionStatus;

    @Column(nullable = false)
    private String tnxRef;

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
