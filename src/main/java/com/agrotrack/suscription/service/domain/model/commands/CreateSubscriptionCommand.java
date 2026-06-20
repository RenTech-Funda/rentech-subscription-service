package com.agrotrack.suscription.service.domain.model.commands;

import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionPlan;

import java.time.LocalDate;

public record CreateSubscriptionCommand(
        SubscriptionPlan subscriptionPlan,
        LocalDate startDate,
        LocalDate endDate,
        String organizationName,
        Long ownerUserId
) {
    public CreateSubscriptionCommand {
        if (subscriptionPlan == null) {
            throw new IllegalArgumentException("Subscription plan is required");
        }
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (organizationName == null || organizationName.isBlank()) {
            throw new IllegalArgumentException("Organization name is required");
        }
        new com.agrotrack.suscription.service.domain.model.valueobjects.UserId(ownerUserId);
    }
}
