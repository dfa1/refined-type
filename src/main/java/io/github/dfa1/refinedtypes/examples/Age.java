package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedInt;

/// Human age in whole years, in the closed interval [0, 150].
///
/// Upper bound chosen above the verified human longevity record
/// (Jeanne Calment, 122) with headroom, so the type rejects sentinel
/// values like `999` or accidental negatives from buggy parsers.
public value class Age implements RefinedInt<Age> {

    public static final int MAX = 150;

    private final int value;

    public Age(int value) {
        if (value < 0 || value > MAX) {
            throw new IllegalArgumentException("age must be in [0, " + MAX + "]: " + value);
        }
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return "Age(" + value + ")";
    }
}
