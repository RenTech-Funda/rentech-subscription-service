package com.agrotrack.suscription.service.domain.model.commands;

public record ActivateSubscriptionCommand(
        Long subscriptionId,
        Long ownerUserId
) {
    public ActivateSubscriptionCommand {
        new com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionId(subscriptionId);
        new com.agrotrack.suscription.service.domain.model.valueobjects.UserId(ownerUserId);
    }
}
