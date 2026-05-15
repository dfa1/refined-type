package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedFloat;

/**
 * Traded quantity. Must be non-negative and finite.
 *
 * <p>Zero is allowed because it is a meaningful state — an order that
 * has not yet executed, or a cancelled remainder. Negative volume is
 * not: signed flow is modelled with a separate side/direction field
 * to keep magnitude and direction independently typed.
 *
 * <p>Modelled as {@code float} to support fractional sizes (FX, crypto,
 * partial shares). Integer-only venues should compose a different type.
 */
public value class Volume implements RefinedFloat {

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

    /** Add two volumes. */
    public Volume plus(Volume other) {
        return new Volume(this.value + other.value);
    }

    @Override
    public String toString() {
        return "Volume(" + value + ")";
    }
}
