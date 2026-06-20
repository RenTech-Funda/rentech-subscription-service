package com.agrotrack.suscription.service.domain.model.commands;

public record CancelSubscriptionCommand(
        Long subscriptionId,
        Long ownerUserId
) {
    public CancelSubscriptionCommand {
        new com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionId(subscriptionId);
        new com.agrotrack.suscription.service.domain.model.valueobjects.UserId(ownerUserId);
    }
}
