package com.salapp.sb.ats.notificationservice.repositories;

import com.salapp.sb.ats.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Notification} entities.
 * <p>
 * This interface extends {@link JpaRepository}, providing CRUD operations and additional query methods
 * for the {@link Notification} entity, identified by its primary key of type {@link Long}.
 * It serves as a data access layer for the notification service, allowing interaction with the
 * underlying database in a type-safe manner without requiring manual implementation of standard methods.
 * </p>
 *
 * @author Stainley Lebron
 * @since March 10, 2025
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
