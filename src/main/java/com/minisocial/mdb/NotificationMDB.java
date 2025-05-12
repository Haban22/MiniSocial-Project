package com.minisocial.mdb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

import java.util.logging.Logger;

/**
 * Message-Driven Bean that consumes notification events from the JMS queue
 * and prints them to the console for verification.
 */
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/Notifications"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
                @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
        }
)
public class NotificationMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(NotificationMDB.class.getName());

    /**
     * Processes incoming JMS messages from the Notifications queue.
     * Expects TextMessages containing JSON-serialized NotificationEvent objects.
     *
     * @param message The JMS message received from the queue.
     */
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String json = textMessage.getText();
                LOGGER.info("Received Notification: " + json);
            } else {
                LOGGER.warning("Received non-text message: " + message.getClass().getName());
            }
        } catch (Exception e) {
            LOGGER.severe("Error processing notification: " + e.getMessage());
        }
    }
}