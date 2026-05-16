package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedFloat;

/// Market price of a financial instrument. Must be strictly positive
/// and finite.
///
/// Zero is excluded because a zero price has no economic meaning
/// in continuous markets and usually signals a missing tick or a
/// parsing bug. For instruments that quote in negative space (e.g.
/// spreads, basis), use a different type — {@link Price} is for
/// outright quotes.
///
/// Note: `float` is used here for benchmarking parity with
/// other {@link RefinedFloat} examples. Production code handling
/// money should prefer fixed-point or `BigDecimal` to avoid
/// binary-floating-point rounding.
public value class Price implements RefinedFloat {

    private final float value;

    public Price(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value) || value <= 0f) {
            throw new IllegalArgumentException("price must be finite and strictly positive: " + value);
        }
        this.value = value;
    }

    @Override
    public float value() {
        return value;
    }

    /// Multiply price by a quantity to get notional.
    public float notional(Volume v) {
        return value * v.value();
    }

    @Override
    public String toString() {
        return "Price(" + value + ")";
    }
}
