package com.salapp.sb.ats.notificationservice.services;

import com.salapp.career.launch.shared.library.NotificationRequest;
import com.salapp.sb.ats.notificationservice.model.Notification;
import com.salapp.sb.ats.notificationservice.repositories.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

/**
 * Service class responsible for handling notification-related operations, including consuming Kafka events,
 * generating email content, sending emails, and persisting notification records.
 * <p>
 * This class integrates with Kafka to listen for notification events, uses Thymeleaf for email template rendering,
 * and leverages Spring Mail for email delivery. It also persists notification details to the database via
 * {@link NotificationRepository}. The service supports multiple notification types (e.g., "activation", "welcome")
 * and handles both successful and failed delivery attempts.
 * </p>
 *
 * @author Stainley Lebron
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;

    @Value("${app.base-url}")
    private String mainServiceUrl;

    /**
     * Listens for notification events from a Kafka topic and processes them by generating and sending emails.
     * <p>
     * This method is triggered by Kafka events on the configured topic. It extracts the {@link NotificationRequest}
     * from the event, generates email content based on the message type, sends the email, and saves the notification
     * status to the database. If email sending fails, it logs the error and records the failure.
     * </p>
     *
     * @param record the Kafka consumer record containing the notification key and {@link NotificationRequest} value
     */
    @KafkaListener(
            topics = "${spring.kafka.topic.notification}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenNotificationEvents(ConsumerRecord<String, NotificationRequest> record) {

        log.info("Received notification event for key {}: {}", record.key(), record.value());

        NotificationRequest request = record.value();

        String recipient = request.getRecipient();
        String messageType = request.getMessageType();
        NotificationRequest.NotificationData data = request.getData();

        try {
            String htmlContent = generateEmailContent(messageType, data);
            sendEmail(recipient, messageType, htmlContent);
            saveNotification(recipient, messageType, htmlContent, true);
        } catch (MessagingException e) {
            log.error("Failed to send {} notification to {}", messageType, recipient);
            saveNotification(recipient, messageType, null, false);
        }
    }

    /**
     * Generates HTML email content based on the specified message type and notification data.
     * <p>
     * Uses Thymeleaf to render email templates (e.g., "activation-email" or "welcome-email") with dynamic data
     * provided in the {@link NotificationRequest.NotificationData} object. The method supports predefined message
     * types and throws an exception for unrecognized types.
     * </p>
     *
     * @param messageType the type of notification (e.g., "activation", "welcome")
     * @param data        the notification data containing dynamic values for the email template
     * @return the rendered HTML content for the email
     * @throws IllegalArgumentException if the message type is unknown or unsupported
     */
    public String generateEmailContent(String messageType, NotificationRequest.NotificationData data) {
        Context context = new Context();
        context.setVariable("firstName", data.getFirstName());

        return switch (messageType) {
            case "activation" -> {
                context.setVariable("activationLink", mainServiceUrl + "/activate?token=" + data.getToken());
                context.setVariable("expiryDate", data.getExpiry());
                yield templateEngine.process("activation-email", context);
            }
            case "welcome" -> templateEngine.process("welcome-email", context);
            default -> throw new IllegalArgumentException("Unknown message type: " + messageType);
        };
    }

    /**
     * Sends an email to the specified recipient with the given message type and HTML content.
     * <p>
     * Constructs a {@link MimeMessage} using {@link JavaMailSender}, sets the recipient, subject, and HTML content,
     * and sends the email. Logs the successful send operation.
     * </p>
     *
     * @param to          the email address of the recipient
     * @param messageType the type of notification (e.g., "activation", "welcome") used to determine the subject
     * @param htmlContent the HTML content of the email body
     * @throws MessagingException if there is an error during email construction or sending
     */
    public void sendEmail(String to, String messageType, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(getSubject(messageType));
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("Sent {} notification to {}", messageType, to);
    }


    /**
     * Retrieves the email subject line based on the specified message type.
     * <p>
     * Provides a predefined subject for known message types ("activation", "welcome") and a default subject
     * for unrecognized types.
     * </p>
     *
     * @param messageType the type of notification (e.g., "activation", "welcome")
     * @return the subject line for the email
     */
    public String getSubject(String messageType) {
        return switch (messageType) {
            case "activation" -> "Activate Your CareerLaunch Account";
            case "welcome" -> "Welcome to CareerLaunch!";
            default -> "CareerLaunch Notification";
        };
    }

    /**
     * Saves a notification record to the database with the specified details.
     * <p>
     * Creates a {@link Notification} entity with the recipient, message type, content, current timestamp,
     * and success status, then persists it using {@link NotificationRepository}.
     * </p>
     *
     * @param recipient   the email address of the notification recipient
     * @param messageType the type of notification (e.g., "activation", "welcome")
     * @param content     the HTML content of the notification, or null if sending failed
     * @param success     true if the notification was sent successfully, false otherwise
     */
    public void saveNotification(String recipient, String messageType, String content, boolean success) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .messageType(messageType)
                .content(content)
                .sentAt(LocalDateTime.now())
                .success(success)
                .build();
        notificationRepository.save(notification);
    }
}
