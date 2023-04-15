package com.example.demo.repository;

import java.util.Optional;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query(value = "INSERT into User u (u.email, u.auth_type) VALUES(?1, ?2)", nativeQuery = true)
    void saveUserLoginByGoogle(String email, String authType);
}
