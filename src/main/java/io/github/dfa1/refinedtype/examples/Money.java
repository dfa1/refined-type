package io.github.dfa1.refinedtype.examples;

/// An amount of money: a {@link Price} paired with a {@link CurrencyCode}.
///
/// Arithmetic operations (`add`, `subtract`, `multiply`) require matching
/// currencies — mixing currencies throws `IllegalArgumentException`. No
/// exchange-rate conversion is performed; that belongs in application logic.
///
/// The amount may be negative (overdrafts, negative P&L, negative-yield bonds).
/// See {@link Price} for the sign semantics.
public value class Money {

    private final Price amount;
    private final CurrencyCode currency;

    private Money(Price amount, CurrencyCode currency) {
        if (amount == null) {
            throw new IllegalArgumentException("amount must not be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency must not be null");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(Price amount, CurrencyCode currency) {
        return new Money(amount, currency);
    }

    public static Money of(double amount, CurrencyCode currency) {
        return new Money(Price.of(amount), currency);
    }

    public Price amount() {
        return amount;
    }

    public CurrencyCode currency() {
        return currency;
    }

    /// Returns `this + other`. Currencies must match.
    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(Price.of(amount.value() + other.amount.value()), currency);
    }

    /// Returns `this - other`. Currencies must match.
    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(Price.of(amount.value() - other.amount.value()), currency);
    }

    /// Returns this amount scaled by `factor`.
    public Money multiply(double factor) {
        return new Money(Price.of(amount.value() * factor), currency);
    }

    /// Returns the additive inverse (negated amount, same currency).
    public Money negate() {
        return new Money(Price.of(-amount.value()), currency);
    }

    public boolean isPositive() {
        return amount.value() > 0.0;
    }

    public boolean isNegative() {
        return amount.value() < 0.0;
    }

    public boolean isZero() {
        return amount.value() == 0.0;
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "currency mismatch: " + currency.value() + " vs " + other.currency.value());
        }
    }

    @Override
    public String toString() {
        return "Money(" + amount.value() + " " + currency.value() + ")";
    }
}
