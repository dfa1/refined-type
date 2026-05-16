package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedDouble;

/// Market price of a financial instrument. Must be strictly positive
/// and finite.
///
/// Zero is excluded because a zero price has no economic meaning
/// in continuous markets and usually signals a missing tick or a
/// parsing bug. For instruments that quote in negative space (e.g.
/// spreads, basis), use a different type — {@link Price} is for
/// outright quotes.
///
/// Note: `double` gives ~15 significant decimal digits — sufficient
/// for most pricing engines. Production code handling money should
/// still consider fixed-point or `BigDecimal` for exact decimal arithmetic.
public value class Price implements RefinedDouble<Price> {

    private final double value;

    public Price(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value <= 0.0) {
            throw new IllegalArgumentException("price must be finite and strictly positive: " + value);
        }
        this.value = value;
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    public String toString() {
        return "Price(" + value + ")";
    }
}
