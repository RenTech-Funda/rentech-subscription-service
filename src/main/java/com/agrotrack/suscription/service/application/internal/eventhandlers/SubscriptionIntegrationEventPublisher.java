package com.agrotrack.suscription.service.application.internal.eventhandlers;

import com.agrotrack.suscription.service.domain.model.events.SubscriptionActivatedEvent;
import com.agrotrack.suscription.service.domain.model.events.SubscriptionCancelledEvent;
import com.agrotrack.suscription.service.domain.model.events.SubscriptionCreatedEvent;
import com.agrotrack.suscription.service.domain.model.events.SubscriptionExpiredEvent;
import com.agrotrack.suscription.service.infrastructure.broker.SubscriptionRabbitTopology;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Date;
import java.util.UUID;

@Component
public class SubscriptionIntegrationEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionIntegrationEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public SubscriptionIntegrationEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(SubscriptionCreatedEvent event) {
        publish(SubscriptionRabbitTopology.CREATED_ROUTING_KEY, event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(SubscriptionActivatedEvent event) {
        publish(SubscriptionRabbitTopology.ACTIVATED_ROUTING_KEY, event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(SubscriptionCancelledEvent event) {
        publish(SubscriptionRabbitTopology.CANCELLED_ROUTING_KEY, event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(SubscriptionExpiredEvent event) {
        publish(SubscriptionRabbitTopology.EXPIRED_ROUTING_KEY, event);
    }

    private void publish(String routingKey, Object event) {
        try {
            var message = MessageBuilder
                    .withBody(objectMapper.writeValueAsBytes(event))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .setMessageId(UUID.randomUUID().toString())
                    .setTimestamp(new Date())
                    .setHeader("eventType", routingKey)
                    .setHeader("eventVersion", 1)
                    .build();

            rabbitTemplate.send(SubscriptionRabbitTopology.EXCHANGE, routingKey, message);
            LOGGER.info("Published RabbitMQ event {} with messageId {}",
                    routingKey, message.getMessageProperties().getMessageId());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize integration event " + routingKey, exception);
        }
    }
}
