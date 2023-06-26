package com.example.demo.repository;

import com.example.demo.entity.AccountExam;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountExamRepository extends JpaRepository<AccountExam, Long> {
	Optional<AccountExam> findAccountExamByUsernameAndPassword(String username, String pw);

	void deleteByEndAtBefore(LocalDateTime now);
}
