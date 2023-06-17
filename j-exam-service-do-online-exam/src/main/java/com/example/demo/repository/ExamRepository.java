package com.example.demo.repository;

import com.example.demo.entity.Exam;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>, AbstractRepository<Exam> {
	Optional<Exam> findExamByExamName(String name);

	List<Exam> findAllByOwnerId(Long id);

	@Query(value = "SELECT distinct e.duration from Exam e")
	List<Integer> getListDuration();
}
