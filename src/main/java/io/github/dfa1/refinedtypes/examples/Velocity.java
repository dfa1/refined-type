package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedFloat;

/** Speed in metres per second. Must be non-negative; physics forbids negative speed. */
public value class Velocity implements RefinedFloat {

    private final float value;

    public Velocity(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value) || value < 0f) {
            throw new IllegalArgumentException("velocity must be non-negative and finite: " + value);
        }
        this.value = value;
    }

    @Override
    public float value() {
        return value;
    }

    @Override
    public String toString() {
        return "Velocity(" + value + " m/s)";
    }
}
