package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedInt;

/// Positive integer — the canonical minimal example of a value-class refined type.
///
/// Accepts any `int` strictly greater than zero. This is the simplest possible
/// `RefinedInt` implementation and is used in documentation to illustrate the
/// pattern before introducing domain-specific types such as {@link Age} or
/// {@link Port}.
///
/// For a real domain, prefer a named type with a tighter constraint
/// (e.g. `Age`, `Port`, `Percentage`) over a bare `PositiveInt`.
public value class PositiveInt implements RefinedInt<PositiveInt> {

    private final int value;

    public PositiveInt(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("value must be positive (> 0): " + value);
        }
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return "PositiveInt(" + value + ")";
    }
}
