package com.example.demo.repository;

import com.example.demo.entity.Contest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository
		extends JpaRepository<Contest, Long>, AbstractRepository<Contest> {

	List<Contest> findAllByExamIdAndEndAtAfter(Long examID, LocalDateTime timeNow);

	void deleteAllByExamId(Long id);

	List<Contest> getContestsByCreatedBy(String username);
}