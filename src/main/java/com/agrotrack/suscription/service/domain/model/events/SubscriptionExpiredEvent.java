package com.agrotrack.suscription.service.domain.model.events;

public record SubscriptionExpiredEvent(
        Long subscriptionId
) {
}
