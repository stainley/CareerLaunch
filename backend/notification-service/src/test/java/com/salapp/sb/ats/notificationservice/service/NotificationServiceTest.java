package com.salapp.sb.ats.notificationservice.service;


import com.salapp.career.launch.shared.library.NotificationRequest;
import com.salapp.sb.ats.notificationservice.model.Notification;
import com.salapp.sb.ats.notificationservice.repositories.NotificationRepository;
import com.salapp.sb.ats.notificationservice.services.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        // Set the private field using reflection or Mockito
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService(mailSender, templateEngine, notificationRepository);
        // Set the base URL using reflection or setter if available
        try {
            var field = NotificationService.class.getDeclaredField("mainServiceUrl");
            field.setAccessible(true);
            field.set(notificationService, BASE_URL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testListenNotificationEvents_Activation_Success() throws MessagingException {
        // Arrange
        NotificationRequest.NotificationData data = new NotificationRequest.NotificationData("John", "token123", "2025-03-10");
        NotificationRequest request = new NotificationRequest("john@example.com", "activation", data);
        ConsumerRecord<String, NotificationRequest> record = new ConsumerRecord<>("topic", 0, 0, "key", request);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("activation-email"), any(Context.class))).thenReturn("<html>Activation</html>");

        // Act
        notificationService.listenNotificationEvents(record);

        // Assert
        verify(mailSender).send(mimeMessage);
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals("john@example.com", savedNotification.getRecipient());
        assertEquals("activation", savedNotification.getMessageType());
        assertTrue(savedNotification.isSuccess());
        assertNotNull(savedNotification.getSentAt());
    }

    @Disabled
    @Test
    void testListenNotificationEvents_Welcome_Failure() throws MessagingException {
        // Arrange
        NotificationRequest.NotificationData data = new NotificationRequest.NotificationData("Jane", null, null);
        NotificationRequest request = new NotificationRequest("jane@example.com", "welcome", data);
        ConsumerRecord<String, NotificationRequest> record = new ConsumerRecord<>("topic", 0, 0, "key", request);

        when(mailSender.createMimeMessage()).thenThrow(new MessagingException("Email server down"));
        when(templateEngine.process(eq("welcome-email"), any(Context.class))).thenReturn("<html>Welcome</html>");

        // Act
        notificationService.listenNotificationEvents(record);

        // Assert
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals("jane@example.com", savedNotification.getRecipient());
        assertEquals("welcome", savedNotification.getMessageType());
        assertFalse(savedNotification.isSuccess());
        assertNull(savedNotification.getContent());
    }

    @Test
    void testGenerateEmailContent_Activation() {
        // Arrange
        NotificationRequest.NotificationData data = new NotificationRequest.NotificationData("John", "token123", "2025-03-10");
        when(templateEngine.process(eq("activation-email"), any(Context.class))).thenReturn("<html>Activation</html>");

        // Act
        String content = notificationService.generateEmailContent("activation", data);

        // Assert
        verify(templateEngine).process(eq("activation-email"), any(Context.class));
        assertEquals("<html>Activation</html>", content);
    }

    @Test
    void testGenerateEmailContent_UnknownType() {
        // Arrange
        NotificationRequest.NotificationData data = new NotificationRequest.NotificationData("John", null, null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.generateEmailContent("unknown", data));
    }

    @Disabled
    @Test
    void testSendEmail_Success() throws MessagingException {
        // Arrange
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        notificationService.sendEmail("test@example.com", "welcome", "<html>Welcome</html>");

        // Assert
        verify(mailSender).send(mimeMessage);
        verify(mimeMessage).setSubject("Welcome to CareerLaunch!");
    }

    @Test
    void testGetSubject() {
        // Act & Assert
        assertEquals("Activate Your CareerLaunch Account", notificationService.getSubject("activation"));
        assertEquals("Welcome to CareerLaunch!", notificationService.getSubject("welcome"));
        assertEquals("CareerLaunch Notification", notificationService.getSubject("unknown"));
    }

    @Test
    void testSaveNotification() {
        // Arrange
        String recipient = "test@example.com";
        String messageType = "welcome";
        String content = "<html>Welcome</html>";

        // Act
        notificationService.saveNotification(recipient, messageType, content, true);

        // Assert
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification savedNotification = notificationCaptor.getValue();

        assertEquals(recipient, savedNotification.getRecipient());
        assertEquals(messageType, savedNotification.getMessageType());
        assertEquals(content, savedNotification.getContent());
        assertTrue(savedNotification.isSuccess());
        assertNotNull(savedNotification.getSentAt());
    }
}
