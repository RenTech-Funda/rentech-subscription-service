package com.agrotrack.suscription.service.domain.model.events;

public record SubscriptionCreatedEvent(
        Long subscriptionId,
        String organizationName,
        Integer maxPlots,
        Long ownerProfileId
) {
}
