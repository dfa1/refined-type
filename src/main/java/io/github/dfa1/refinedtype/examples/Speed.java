package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedFloat;

/// Speed in meters per second. Must be non-negative; speed is the magnitude
/// of a velocity vector and cannot be negative.
///
/// For signed 1-D motion (where direction matters) use a plain `float` or a
/// dedicated vector type instead.
public value class Speed implements RefinedFloat<Speed> {

    private final float value;

    private Speed(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value) || value < 0f) {
            throw new IllegalArgumentException("speed must be non-negative and finite: " + value);
        }
        this.value = value;
    }

    public static Speed of(float value) {
        return new Speed(value);
    }

    @Override
    public float value() {
        return value;
    }

    @Override
    public String toString() {
        return "Speed(" + value + " m/s)";
    }
}
