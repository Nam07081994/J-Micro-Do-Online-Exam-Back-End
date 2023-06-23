package com.jnotificationservicedoonlineexam.repository;

import com.jnotificationservicedoonlineexam.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository
		extends JpaRepository<Notification, Long>, AbstractRepository<Notification> {}
