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

	List<Exam> findAllByExamTypeIsNotLike(String examType);

	List<Exam> findAllByOwnerId(Long id);

	@Query(value = "SELECT distinct e.duration from Exam e")
	List<Integer> getListDuration();

	@Query(
			value =
					"SELECT * FROM (SELECT *,ROW_NUMBER() OVER (PARTITION BY category_id ORDER BY id) AS row_num FROM tbl_exams WHERE exam_type NOT LIKE '%PRIVATE%') AS subquery WHERE row_num <= 5",
			nativeQuery = true)
	List<Exam> fetchExamByCategory();
}
