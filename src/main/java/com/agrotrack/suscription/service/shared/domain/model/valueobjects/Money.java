package com.agrotrack.suscription.service.shared.domain.model.valueobjects;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Represents a monetary amount with a currency.
 * This value object is immutable and shared across the bounded contexts.
 *
 * @param amount    the monetary amount, it must not be null and must be greater than or equal to zero
 * @param currency  the currency, it must not be null
 *
 * @author Open Source Application Development Team
 */
public record Money(BigDecimal amount, Currency currency) {
    /**
     * Constructs a Money object with validation.
     *
     * @param amount    the monetary amount, it must not be null and must be greater than or equal to zero, with decimal digits according to the currency
     * @param currency  the currency, it must not be null
     *
     * @throws IllegalArgumentException if the amount is null or less than zero, or if the currency is null
     */
    public Money {
        if (Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be null or less than zero");
        }
        if (Objects.isNull(currency)) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        if (amount.scale() > currency.getDefaultFractionDigits()) {
            throw new IllegalArgumentException("Amount scale must be less than or equal to the currency fraction digits");
        }
    }

    /**
     * Creates a zero-amount Money instance in USD.
     *
     * @return a Money instance with zero amount value in USD
     */
    public static Money zero() {
        return new Money(BigDecimal.ZERO, Currency.getInstance("USD"));
    }

    /**
     * Adds another Money instance to this one.
     *
     * @param other the Money value to add, must have the same currency
     * @return a new Money instance with the summed amount
     * @throws IllegalArgumentException if currencies differ
     */
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(amount.add(other.amount), currency);
    }

    /**
     * Multiplies this Money instance by a factor.
     *
     * @param multiplier the multiplication factor
     * @return a new Money instance with the multiplied amount
     */
    public Money multiply(int multiplier) {
        return new Money(amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
    }
}
