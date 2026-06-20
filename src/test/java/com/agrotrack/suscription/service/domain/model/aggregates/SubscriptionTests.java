package com.agrotrack.suscription.service.domain.model.aggregates;

import com.agrotrack.suscription.service.domain.model.commands.CreateSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionPlan;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubscriptionTests {

    @Test
    void shouldCreatePendingSmartSubscriptionWithCalculatedBenefits() {
        var subscription = new Subscription(validCommand(SubscriptionPlan.AGRO_SMART));

        assertEquals(SubscriptionStatus.PENDING, subscription.getSubscriptionStatus());
        assertEquals(BigDecimal.valueOf(29), subscription.getPrice().amount());
        assertEquals("USD", subscription.getPrice().currency().getCurrencyCode());
        assertEquals(30, subscription.getMaxPlots());
        assertEquals(10L, subscription.getOwnerUserId().value());
    }

    @Test
    void shouldActivateOnlyPendingSubscription() {
        var subscription = persistedSubscription();

        subscription.activate();

        assertEquals(SubscriptionStatus.ACTIVE, subscription.getSubscriptionStatus());
        assertThrows(IllegalStateException.class, subscription::activate);
    }

    @Test
    void shouldCancelOnlyActiveSubscription() {
        var subscription = persistedSubscription();

        assertThrows(IllegalStateException.class, subscription::cancel);
        subscription.activate();
        subscription.cancel();

        assertEquals(SubscriptionStatus.CANCELLED, subscription.getSubscriptionStatus());
    }

    @Test
    void shouldRejectInvalidDateRange() {
        var today = LocalDate.now();

        assertThrows(IllegalArgumentException.class, () -> new CreateSubscriptionCommand(
                SubscriptionPlan.AGRO_START, today, today, "Farm", 10L));
    }

    private Subscription persistedSubscription() {
        var subscription = new Subscription(validCommand(SubscriptionPlan.AGRO_START));
        ReflectionTestUtils.setField(subscription, "id", 1L);
        return subscription;
    }

    private CreateSubscriptionCommand validCommand(SubscriptionPlan plan) {
        var startDate = LocalDate.now();
        return new CreateSubscriptionCommand(
                plan, startDate, startDate.plusMonths(1), "Agro Farm", 10L);
    }
}
