package com.agrotrack.suscription.service.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record UserId(Long value) {

    public UserId() {
        this(null);
    }

    public UserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("UserId must be a positive non-null value.");
        }
    }
}
