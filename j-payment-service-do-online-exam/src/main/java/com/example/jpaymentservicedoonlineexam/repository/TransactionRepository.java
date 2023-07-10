package com.example.jpaymentservicedoonlineexam.repository;

import com.example.jpaymentservicedoonlineexam.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
