package com.example.demo.repository;

import com.example.demo.entity.Contest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository
		extends JpaRepository<Contest, Long>, AbstractRepository<Contest> {

	List<Contest> getContestsByCreatedBy(String username);
}
