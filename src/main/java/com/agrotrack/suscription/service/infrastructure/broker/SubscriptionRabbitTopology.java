package com.agrotrack.suscription.service.infrastructure.broker;

public final class SubscriptionRabbitTopology {

    public static final String EXCHANGE = "agrotrack.exchange";
    public static final String CREATED_ROUTING_KEY = "subscription.created";
    public static final String ACTIVATED_ROUTING_KEY = "subscription.activated";
    public static final String CANCELLED_ROUTING_KEY = "subscription.cancelled";
    public static final String EXPIRED_ROUTING_KEY = "subscription.expired";

    private SubscriptionRabbitTopology() {
    }
}
