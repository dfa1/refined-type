package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedDouble;

/// Market price of a financial instrument. Must be finite.
///
/// No sign constraint is imposed — negative prices occur in real markets
/// (power oversupply, negative-yield bonds, negative repo rates). Callers
/// that need a strictly positive price should validate at their boundary.
///
/// Note: `double` gives ~15 significant decimal digits — sufficient
/// for most pricing engines. Production code handling money should
/// still consider fixed-point or `BigDecimal` for exact decimal arithmetic.
public value class Price implements RefinedDouble<Price> {

    private final double value;

    public Price(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("price must be finite: " + value);
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
