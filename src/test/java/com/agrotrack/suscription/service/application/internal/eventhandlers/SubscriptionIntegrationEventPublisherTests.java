package com.agrotrack.suscription.service.application.internal.eventhandlers;

import com.agrotrack.suscription.service.domain.model.events.SubscriptionCreatedEvent;
import com.agrotrack.suscription.service.infrastructure.broker.SubscriptionRabbitTopology;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SubscriptionIntegrationEventPublisherTests {

    @Test
    void shouldPublishCreatedEventAsPersistentJsonMessage() throws Exception {
        var rabbitTemplate = mock(RabbitTemplate.class);
        var objectMapper = new ObjectMapper();
        var publisher = new SubscriptionIntegrationEventPublisher(rabbitTemplate, objectMapper);
        var event = new SubscriptionCreatedEvent(25L, "Fundo Norte", 30, 7L);

        publisher.on(event);

        var messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(rabbitTemplate).send(
                org.mockito.ArgumentMatchers.eq(SubscriptionRabbitTopology.EXCHANGE),
                org.mockito.ArgumentMatchers.eq(SubscriptionRabbitTopology.CREATED_ROUTING_KEY),
                messageCaptor.capture());

        var message = messageCaptor.getValue();
        var decoded = objectMapper.readValue(message.getBody(), SubscriptionCreatedEvent.class);
        assertEquals(event, decoded);
        assertEquals("application/json", message.getMessageProperties().getContentType());
        assertEquals(Integer.valueOf(1), message.getMessageProperties().getHeader("eventVersion"));
    }
}
