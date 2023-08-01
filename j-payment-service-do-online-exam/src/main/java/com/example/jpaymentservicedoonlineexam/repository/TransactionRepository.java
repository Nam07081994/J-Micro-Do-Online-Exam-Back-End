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
    @Query(value = "SELECT DISTINCT t1.*\n" +
            "FROM tbl_transactions t1\n" +
        "        JOIN (\n" +
            "    SELECT created_at\\:\\:date AS new_date\n" +
            "    FROM tbl_transactions\n" +
            "    WHERE created_at\\:\\:date = (NOW() - INTERVAL '1 month')\\:\\:DATE\n" +
            ") t2 ON t1.created_at\\:\\:date = t2.new_date\n" +
            "    AND t1.recurring_payment_type = :type", nativeQuery = true)
    List<Transaction> findAllByPaymentTypeMonth(@Param("type") Integer type);

    @Query(value = "SELECT DISTINCT t1.*\n" +
            "FROM tbl_transactions t1\n" +
            "        JOIN (\n" +
            "    SELECT created_at\\:\\:date AS new_date\n" +
            "    FROM tbl_transactions\n" +
            "    WHERE created_at\\:\\:date = (NOW() - INTERVAL '1 year')\\:\\:DATE\n" +
            ") t2 ON t1.created_at\\:\\:date = t2.new_date\n" +
            "    AND t1.recurring_payment_type = :type", nativeQuery = true)
    List<Transaction> findAllByPaymentTypeYear(@Param("type") Integer type);

    List<Transaction> findAllByExpiredDate(LocalDate expriredTime);


    Optional<Transaction> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}
