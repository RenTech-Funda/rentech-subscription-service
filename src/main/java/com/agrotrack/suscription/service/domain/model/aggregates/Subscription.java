package com.agrotrack.suscription.service.domain.model.aggregates;

import com.agrotrack.suscription.service.domain.model.commands.CreateSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.events.SubscriptionActivatedEvent;
import com.agrotrack.suscription.service.domain.model.events.SubscriptionCancelledEvent;
import com.agrotrack.suscription.service.domain.model.events.SubscriptionExpiredEvent;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionId;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionPlan;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionStatus;
import com.agrotrack.suscription.service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.agrotrack.suscription.service.shared.domain.model.valueobjects.Money;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

@Getter
@Entity
public class Subscription extends AuditableAbstractAggregateRoot<Subscription> {
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "subscription_id", unique = true))
    private SubscriptionId subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan")
    private SubscriptionPlan subscriptionPlan;

    private Date startDate;
    private Date endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status")
    private SubscriptionStatus subscriptionStatus;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    })
    private Money price;

    private Integer maxPlots;

    protected Subscription() {
    }

    private Money calculatePrice(SubscriptionPlan plan) {
        if (plan.equals(SubscriptionPlan.AGRO_START)) {
            return new Money(BigDecimal.valueOf(19), Currency.getInstance("USD"));
        } else if (plan.equals(SubscriptionPlan.AGRO_SMART)) {
            return new Money(BigDecimal.valueOf(29), Currency.getInstance("USD"));
        } else if (plan.equals(SubscriptionPlan.AGRO_EXPERT)) {
            return new Money(BigDecimal.valueOf(49), Currency.getInstance("USD"));
        } else {
            throw new IllegalArgumentException("Unknown subscription plan: " + plan);
        }
    }

    private Integer calculateMaxPlots(SubscriptionPlan plan) {
        if (plan.equals(SubscriptionPlan.AGRO_START)) {
            return 10;
        } else if (plan.equals(SubscriptionPlan.AGRO_SMART)) {
            return 30;
        } else if (plan.equals(SubscriptionPlan.AGRO_EXPERT)) {
            return 90;
        } else {
            throw new IllegalArgumentException("Unknown subscription plan: " + plan);
        }
    }

    public Subscription(CreateSubscriptionCommand command) {
        this.subscriptionPlan = command.subscriptionPlan();
        this.startDate = command.startDate();
        this.endDate = command.endDate();
        this.subscriptionStatus = SubscriptionStatus.PENDING;
        this.price = calculatePrice(command.subscriptionPlan());
        this.maxPlots = calculateMaxPlots(command.subscriptionPlan());
    }

    @PostPersist
    protected void onPostPersist() {
        this.subscriptionId = new SubscriptionId(this.getId());
    }

    public void activate() {
        if(this.subscriptionStatus != SubscriptionStatus.PENDING) {
            throw new IllegalStateException("Only pending subscriptions can be activated.");
        }
        this.subscriptionStatus = SubscriptionStatus.ACTIVE;
        this.registerEvent(new SubscriptionActivatedEvent(this.subscriptionId.value()));
    }

    public void expire() {
        if(this.subscriptionStatus != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Only active subscriptions can be expired.");
        }
        this.subscriptionStatus = SubscriptionStatus.EXPIRED;
        this.registerEvent(new SubscriptionExpiredEvent(this.subscriptionId.value()));
    }

    public void cancel() {
        if(this.subscriptionStatus != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Only active subscriptions can be cancelled.");
        }
        this.subscriptionStatus = SubscriptionStatus.CANCELLED;
        this.registerEvent(new SubscriptionCancelledEvent(this.subscriptionId.value()));
    }

}
