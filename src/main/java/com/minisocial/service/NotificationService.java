package com.minisocial.service;

import com.minisocial.dto.NotificationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.*;

import java.util.logging.Logger;

@Stateless
public class NotificationService {

    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/jms/queue/Notifications")
    private Queue queue;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends a notification event to the JMS queue as a JSON message.
     *
     * @param eventType       The type of event (e.g., FRIEND_REQUEST, POST_LIKED).
     * @param sourceUserId    The ID of the user triggering the event.
     * @param targetUserId    The ID of the user receiving the notification.
     * @param relatedEntityId The ID of the related entity (e.g., post ID, group ID).
     * @param details         Additional details about the event.
     */
    public void sendNotification(String eventType, Long sourceUserId, Long targetUserId, Long relatedEntityId, String details) {
        NotificationEvent event = new NotificationEvent(eventType, sourceUserId, targetUserId, relatedEntityId, details);
        Connection connection = null;
        Session session = null;
        try {
            // Serialize event to JSON
            String jsonMessage;
            try {
                jsonMessage = objectMapper.writeValueAsString(event);
            } catch (Exception e) {
                LOGGER.severe("Failed to serialize notification event: " + e.getMessage());
                throw new RuntimeException("Failed to serialize notification event", e);
            }

            // Send JMS message
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage(jsonMessage);
            producer.send(message);
            LOGGER.info("Sent notification: " + jsonMessage);
        } catch (JMSException e) {
            LOGGER.severe("Failed to send JMS notification: " + e.getMessage());
            throw new RuntimeException("Failed to send JMS notification", e);
        } finally {
            // Clean up JMS resources
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    LOGGER.warning("Failed to close JMS session: " + e.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    LOGGER.warning("Failed to close JMS connection: " + e.getMessage());
                }
            }
        }
    }
}