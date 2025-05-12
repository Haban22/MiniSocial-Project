package com.minisocial.service;

import com.minisocial.dto.NotificationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.*;

@Stateless
public class NotificationService {

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/jms/queue/Notifications")
    private Queue queue;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendNotification(String eventType, Long sourceUserId, Long targetUserId, Long relatedEntityId, String details) {
        NotificationEvent event = new NotificationEvent(eventType, sourceUserId, targetUserId, relatedEntityId, details);
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage(objectMapper.writeValueAsString(event));
            producer.send(message);
            session.close();
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException("Failed to send JMS notification", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize notification event", e);
        }
    }
}