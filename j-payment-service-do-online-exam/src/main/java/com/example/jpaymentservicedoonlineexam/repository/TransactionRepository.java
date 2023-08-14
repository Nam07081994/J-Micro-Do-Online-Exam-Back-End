package com.example.jpaymentservicedoonlineexam.repository;

import com.example.jpaymentservicedoonlineexam.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findAllByExpiredDate(LocalDate expriredTime);


    Optional<Transaction> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Transaction> findByTransactionNo(String transactionNo);
}
