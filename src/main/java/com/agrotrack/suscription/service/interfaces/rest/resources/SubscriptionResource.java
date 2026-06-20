package com.agrotrack.suscription.service.interfaces.rest.resources;

import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionPlan;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public record SubscriptionResource(
        Long subscriptionId,
        SubscriptionPlan subscriptionPlan,
        LocalDate startDate,
        LocalDate endDate,
        SubscriptionStatus subscriptionStatus,
        BigDecimal priceAmount,
        String priceCurrency,
        Integer maxPlots,
        Long ownerUserId,
        Date createdAt,
        Date updatedAt
) {
}
