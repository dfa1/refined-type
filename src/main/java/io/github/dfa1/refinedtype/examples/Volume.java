package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedFloat;

/// Traded quantity. Must be non-negative and finite.
///
/// Zero is allowed because it is a meaningful state — an order that
/// has not yet executed, or a canceled remainder. Negative volume is
/// not: signed flow is modeled with a separate side/direction field
/// to keep magnitude and direction independently typed.
///
/// Modeled as `float` to support fractional sizes (FX, crypto,
/// partial shares). Integer-only venues should compose a different type.
public value class Volume implements RefinedFloat<Volume> {

    public static final Volume ZERO = new Volume(0f);

    private final float value;

    public Volume(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value) || value < 0f) {
            throw new IllegalArgumentException("volume must be finite and non-negative: " + value);
        }
        this.value = value;
    }

    @Override
    public float value() {
        return value;
    }

    /// Add two volumes.
    public Volume plus(Volume other) {
        return new Volume(this.value + other.value);
    }

    @Override
    public String toString() {
        return "Volume(" + value + ")";
    }
}
