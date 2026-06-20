package com.agrotrack.suscription.service.interfaces.rest;

import com.agrotrack.suscription.service.domain.model.commands.ActivateSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.commands.CancelSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.commands.CreateSubscriptionCommand;
import com.agrotrack.suscription.service.domain.model.valueobjects.SubscriptionId;
import com.agrotrack.suscription.service.domain.model.valueobjects.UserId;
import com.agrotrack.suscription.service.domain.services.SubscriptionCommandService;
import com.agrotrack.suscription.service.domain.services.SubscriptionQueryService;
import com.agrotrack.suscription.service.interfaces.rest.resources.CreateSubscriptionResource;
import com.agrotrack.suscription.service.interfaces.rest.resources.SubscriptionResource;
import com.agrotrack.suscription.service.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import com.agrotrack.suscription.service.shared.infrastructure.security.AuthenticatedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/api/v1/subscriptions", produces = "application/json")
@Tag(name = "Subscriptions", description = "Subscription management endpoints")
public class SubscriptionController {

    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionQueryService subscriptionQueryService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public SubscriptionController(SubscriptionCommandService subscriptionCommandService,
                                  SubscriptionQueryService subscriptionQueryService,
                                  AuthenticatedUserProvider authenticatedUserProvider) {
        this.subscriptionCommandService = subscriptionCommandService;
        this.subscriptionQueryService = subscriptionQueryService;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @PostMapping(consumes = "application/json")
    @Operation(summary = "Create a pending subscription")
    public ResponseEntity<SubscriptionResource> createSubscription(
            @Valid @RequestBody CreateSubscriptionResource resource) {

        var userId = authenticatedUserProvider.getUserId();
        var startDate = LocalDate.now();
        var command = new CreateSubscriptionCommand(
                resource.subscriptionPlan(),
                startDate,
                startDate.plusMonths(resource.durationMonths()),
                resource.organizationName().trim(),
                userId
        );

        var subscriptionId = subscriptionCommandService.handle(command);
        var subscription = subscriptionQueryService
                .getBySubscriptionIdAndOwnerUserId(new SubscriptionId(subscriptionId), new UserId(userId))
                .orElseThrow(() -> new IllegalStateException("Created subscription could not be retrieved"));

        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{subscriptionId}")
                .buildAndExpand(subscriptionId)
                .toUri();

        return ResponseEntity.created(location)
                .body(SubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription));
    }

    @GetMapping("/mine")
    @Operation(summary = "Get subscriptions owned by the authenticated user")
    public ResponseEntity<List<SubscriptionResource>> getMySubscriptions() {
        var userId = authenticatedUserProvider.getUserId();
        var resources = subscriptionQueryService.getByOwnerUserId(new UserId(userId)).stream()
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{subscriptionId}")
    @Operation(summary = "Get one subscription owned by the authenticated user")
    public ResponseEntity<SubscriptionResource> getSubscription(
            @PathVariable @Positive Long subscriptionId) {
        var userId = authenticatedUserProvider.getUserId();
        return subscriptionQueryService
                .getBySubscriptionIdAndOwnerUserId(new SubscriptionId(subscriptionId), new UserId(userId))
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{subscriptionId}/activate")
    @Operation(summary = "Simulate payment approval and activate a pending subscription")
    public ResponseEntity<SubscriptionResource> activateSubscription(
            @PathVariable @Positive Long subscriptionId) {
        var userId = authenticatedUserProvider.getUserId();
        return updateStatus(
                subscriptionCommandService.handle(new ActivateSubscriptionCommand(subscriptionId, userId)),
                subscriptionId,
                userId
        );
    }

    @PutMapping("/{subscriptionId}/cancel")
    @Operation(summary = "Cancel an active subscription")
    public ResponseEntity<SubscriptionResource> cancelSubscription(
            @PathVariable @Positive Long subscriptionId) {
        var userId = authenticatedUserProvider.getUserId();
        return updateStatus(
                subscriptionCommandService.handle(new CancelSubscriptionCommand(subscriptionId, userId)),
                subscriptionId,
                userId
        );
    }

    private ResponseEntity<SubscriptionResource> updateStatus(
            java.util.Optional<Long> updatedSubscriptionId,
            Long subscriptionId,
            Long userId) {
        if (updatedSubscriptionId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return subscriptionQueryService
                .getBySubscriptionIdAndOwnerUserId(new SubscriptionId(subscriptionId), new UserId(userId))
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
