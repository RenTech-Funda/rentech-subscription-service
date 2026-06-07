package com.agrotrack.suscription.service.application.internal.commandservice;

import com.agrotrack.suscription.service.domain.model.aggregates.Subscription;
import com.agrotrack.suscription.service.domain.model.commands.ActivateSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.commands.CancelSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.commands.CreateSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.commands.ExpireSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.events.SubscriptionCreatedEvent;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionId;
import com.agrotrack.suscription.service.domain.services.SubscriptionCommandService;
import com.agrotrack.suscription.service.infrastructure.persistence.jpa.repositories.SubscriptionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository,
                                          ApplicationEventPublisher eventPublisher) {
        this.subscriptionRepository = subscriptionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Long handle(CreateSubscriptionCommand command) {
        var subscription = new Subscription(command);

        // Save and flush to generate the ID
        var savedSubscription = subscriptionRepository.saveAndFlush(subscription);

        // Now the subscriptionId is available (generated in @PostPersist)
        var event = new SubscriptionCreatedEvent(
                savedSubscription.getSubscriptionId().value(),
                command.organizationName(),
                savedSubscription.getMaxPlots(),
                command.ownerProfileId()
        );

        eventPublisher.publishEvent(event);

        return savedSubscription.getSubscriptionId().value();
    }

    @Override
    public Optional<Long> handle(ActivateSubscriptionCommand command) {
        var subscriptionOpt = subscriptionRepository.findBySubscriptionId(new SubscriptionId(command.subscriptionId()));

        if (subscriptionOpt.isEmpty()) {
            return Optional.empty();
        }

        var subscription = subscriptionOpt.get();
        subscription.activate();
        // Save triggers domain events automatically
        subscriptionRepository.save(subscription);

        return Optional.of(subscription.getSubscriptionId().value());
    }

    @Override
    public Optional<Long> handle(CancelSubscriptionCommand command) {
        var subscriptionOpt = subscriptionRepository.findBySubscriptionId(new SubscriptionId(command.subscriptionId()));

        if (subscriptionOpt.isEmpty()) {
            return Optional.empty();
        }

        var subscription = subscriptionOpt.get();
        subscription.cancel();
        // Save triggers domain events automatically
        subscriptionRepository.save(subscription);

        return Optional.of(subscription.getSubscriptionId().value());
    }

    @Override
    public Optional<Long> handle(ExpireSubscriptionCommand command) {
        var subscriptionOpt = subscriptionRepository.findBySubscriptionId(new SubscriptionId(command.subscriptionId()));

        if (subscriptionOpt.isEmpty()) {
            return Optional.empty();
        }

        var subscription = subscriptionOpt.get();
        subscription.expire();
        // Save triggers domain events automatically
        subscriptionRepository.save(subscription);

        return Optional.of(subscription.getSubscriptionId().value());
    }
}
