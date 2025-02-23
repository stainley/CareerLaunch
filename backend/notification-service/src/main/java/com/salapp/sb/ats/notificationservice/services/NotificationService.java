package com.salapp.sb.ats.notificationservice.services;

import com.salapp.sb.ats.notificationservice.dto.NotificationRequest;
import com.salapp.sb.ats.notificationservice.model.Notification;
import com.salapp.sb.ats.notificationservice.repositories.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final NotificationRepository notificationRepository;

    @Value("${app.base-url}")
    private String mainServiceUrl;

    @KafkaListener(topics = "${spring.kafka.topic.notification}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenNotificationEvents(NotificationRequest request) {
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

    private String generateEmailContent(String messageType, NotificationRequest.NotificationData data) {
        Context context = new Context();
        context.setVariable("firstName", data.getFirstName());

        return switch (messageType) {
            case "activation" -> {
                context.setVariable("activationLink", mainServiceUrl + "/api/v1/users/activate?token=" + data.getToken());
                context.setVariable("expiryDate", data.getExpiry());
                yield templateEngine.process("activation-email", context);
            }
            case "welcome" -> templateEngine.process("welcome-email", context);
            default -> throw new IllegalArgumentException("Unknown message type: " + messageType);
        };
    }

    private void sendEmail(String to, String messageType, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(getSubject(messageType));
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("Sent {} notification to {}", messageType, to);
    }

    private String getSubject(String messageType) {
        return switch (messageType) {
            case "activation" -> "Activate Your CareerLaunch Account";
            case "welcome" -> "Welcome to CareerLaunch!";
            default -> "CareerLaunch Notification";
        };
    }

    private void saveNotification(String recipient, String messageType, String content, boolean success) {
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
