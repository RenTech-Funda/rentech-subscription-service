package com.agrotrack.suscription.service.infrastructure.persistence.jpa.repositories;

import com.agrotrack.suscription.service.domain.model.aggregates.Subscription;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionPlan;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionStatus;
import com.agrotrack.suscription.service.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findBySubscriptionStatus(SubscriptionStatus subscriptionStatus);
    List<Subscription> findBySubscriptionPlan(SubscriptionPlan subscriptionPlan);
    List<Subscription> findBySubscriptionStatusOrderByStartDateDesc(SubscriptionStatus subscriptionStatus);
    List<Subscription> findAllByOwnerUserIdOrderByCreatedAtDesc(UserId ownerUserId);
    Optional<Subscription> findByIdAndOwnerUserId(Long id, UserId ownerUserId);
}
