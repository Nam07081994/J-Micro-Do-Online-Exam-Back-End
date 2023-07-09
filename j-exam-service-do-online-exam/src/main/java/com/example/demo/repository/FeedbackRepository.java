package com.example.demo.repository;

import com.example.demo.entity.Feedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository
		extends JpaRepository<Feedback, Long>, AbstractRepository<Feedback> {

	@Query(
			value =
					"SELECT * FROM tbl_users WHERE user_id=:userID AND examName=:examName UNION ALL"
							+ " (SELECT * FROM tbl_users AS b WHERE user_id <> :userID AND examName=:examName order by b.created_at DESC );",
			countQuery =
					"SELECT * FROM tbl_users WHERE user_id=:userID AND examName=:examName UNION ALL"
							+ " (SELECT * FROM tbl_users AS b WHERE user_id <> :userID AND examName=:examName order by b.created_at DESC );",
			nativeQuery = true)
	Page<Feedback> getFeedbackByUser(Long userID, String examName, Pageable pageable);

	@Query(
			value =
					"SELECT * FROM tbl_users WHERE user_id=:userID AND examName=:examName AND vote_number=:vote UNION ALL"
							+ " (SELECT * FROM tbl_users AS b WHERE user_id <> :userID AND examName=:examName order by b.created_at DESC );",
			countQuery =
					"SELECT * FROM tbl_users WHERE user_id=:userID AND examName=:examName AND vote_number=:vote UNION ALL"
							+ " (SELECT * FROM tbl_users AS b WHERE user_id <> :userID AND examName=:examName order by b.created_at DESC );",
			nativeQuery = true)
	Page<Feedback> getFeedbackByUserVote(
			Long userID, String examName, Integer vote, Pageable pageable);

	Optional<Feedback> findFeedbackByExamIdAndAndUserID(Long examID, Long UserID);

	List<Feedback> findAllByExamId(Long id);
}
