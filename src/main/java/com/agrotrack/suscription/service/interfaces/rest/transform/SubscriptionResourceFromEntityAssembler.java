package com.agrotrack.suscription.service.interfaces.rest.transform;

import com.agrotrack.suscription.service.domain.model.aggregates.Subscription;
import com.agrotrack.suscription.service.interfaces.rest.resources.SubscriptionResource;

public final class SubscriptionResourceFromEntityAssembler {

    private SubscriptionResourceFromEntityAssembler() {
    }

    public static SubscriptionResource toResourceFromEntity(Subscription entity) {
        return new SubscriptionResource(
                entity.getSubscriptionId().value(),
                entity.getSubscriptionPlan(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getSubscriptionStatus(),
                entity.getPrice().amount(),
                entity.getPrice().currency().getCurrencyCode(),
                entity.getMaxPlots(),
                entity.getOwnerUserId().value(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
