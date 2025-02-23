package com.salapp.sb.ats.notificationservice.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {

    private final String recipient;
    private final String messageType;
    private final Object data;

    public NotificationEvent(Object source, String recipient, String messageType, Object data) {
        super(source);
        this.recipient = recipient;
        this.messageType = messageType;
        this.data = data;
    }
}
