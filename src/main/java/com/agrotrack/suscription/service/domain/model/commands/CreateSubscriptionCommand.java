package com.agrotrack.suscription.service.domain.model.commands;

import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionPlan;

import java.util.Date;

public record CreateSubscriptionCommand(
        SubscriptionPlan subscriptionPlan,
        Date startDate,
        Date endDate,
        String organizationName,
        Long ownerProfileId
) {
}
