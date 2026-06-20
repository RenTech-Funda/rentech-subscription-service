package com.agrotrack.suscription.service.interfaces.rest.resources;

import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionPlan;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSubscriptionResource(
        @NotNull(message = "Subscription plan is required")
        SubscriptionPlan subscriptionPlan,

        @NotNull(message = "Duration in months is required")
        @Min(value = 1, message = "Duration must be at least 1 month")
        @Max(value = 12, message = "Duration must not exceed 12 months")
        Integer durationMonths,

        @NotBlank(message = "Organization name is required")
        @Size(max = 100, message = "Organization name must not exceed 100 characters")
        String organizationName
) {
}
