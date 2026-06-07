package com.agrotrack.suscription.service.application.internal.queryservice;

import com.agrotrack.suscription.service.domain.model.aggregates.Subscription;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionId;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionStatus;
import com.agrotrack.suscription.service.domain.services.SubscriptionQueryService;
import com.agrotrack.suscription.service.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionQueryServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Optional<Subscription> getBySubscriptionId(SubscriptionId subscriptionId) {
        return subscriptionRepository.findBySubscriptionId(subscriptionId);
    }

    @Override
    public List<Subscription> getActiveSubscriptions() {
        return subscriptionRepository.findBySubscriptionStatus(SubscriptionStatus.ACTIVE);
    }
}
