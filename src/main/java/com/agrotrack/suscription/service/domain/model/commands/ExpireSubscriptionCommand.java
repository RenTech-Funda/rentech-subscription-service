package com.agrotrack.suscription.service.domain.model.commands;

public record ExpireSubscriptionCommand(
        Long subscriptionId,
        Long ownerUserId
) {
    public ExpireSubscriptionCommand {
        new com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionId(subscriptionId);
        new com.agrotrack.suscription.service.domain.model.valueobjects.UserId(ownerUserId);
    }
}
