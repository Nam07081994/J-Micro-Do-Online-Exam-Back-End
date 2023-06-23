package com.jnotificationservicedoonlineexam.repository;

import com.jnotificationservicedoonlineexam.entity.Topic;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, AbstractRepository<Topic> {
	Optional<Topic> findByTopicName(String name);
}
