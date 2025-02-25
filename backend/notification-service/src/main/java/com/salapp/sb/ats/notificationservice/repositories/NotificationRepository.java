package com.salapp.sb.ats.notificationservice.repositories;

import com.salapp.sb.ats.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
