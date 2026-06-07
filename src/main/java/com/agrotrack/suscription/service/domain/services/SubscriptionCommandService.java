package com.agrotrack.suscription.service.domain.services;

import com.agrotrack.suscription.service.domain.model.commands.ActivateSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.commands.CancelSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.commands.CreateSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.commands.ExpireSubscriptionCommand;

import java.util.Optional;

public interface SubscriptionCommandService {
    Long handle(CreateSubscriptionCommand command);
    Optional<Long> handle(ActivateSubscriptionCommand command);
    Optional<Long> handle(CancelSubscriptionCommand command);
    Optional<Long> handle(ExpireSubscriptionCommand command);
}
