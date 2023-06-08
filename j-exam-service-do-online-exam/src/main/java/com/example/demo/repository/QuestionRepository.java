package com.example.demo.repository;

import java.util.List;

import com.example.demo.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "SELECT q FROM Question q WHERE q.examId = ?1")
    List<Question> findQuestionByExamId(Long examId);
}
