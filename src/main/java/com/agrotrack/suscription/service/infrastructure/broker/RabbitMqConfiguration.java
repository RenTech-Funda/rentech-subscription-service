package com.agrotrack.suscription.service.infrastructure.broker;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    @Bean
    public DirectExchange agrotrackExchange() {
        return new DirectExchange(SubscriptionRabbitTopology.EXCHANGE, true, false);
    }
}
