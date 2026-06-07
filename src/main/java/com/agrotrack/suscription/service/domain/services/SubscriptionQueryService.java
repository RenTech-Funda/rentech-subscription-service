package com.agrotrack.suscription.service.domain.services;

import com.agrotrack.suscription.service.domain.model.aggregates.Subscription;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionId;

import java.util.List;
import java.util.Optional;

public interface SubscriptionQueryService {
    Optional<Subscription> getBySubscriptionId(SubscriptionId subscriptionId);
    List<Subscription> getActiveSubscriptions();
}
