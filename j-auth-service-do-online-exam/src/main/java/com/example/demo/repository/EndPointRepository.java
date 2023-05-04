package com.example.demo.repository;

import java.util.Optional;

import com.example.demo.entity.EndPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EndPointRepository extends JpaRepository<EndPoint, Long> {
    Optional<EndPoint> findByEndPoint(String endPoint);
}
