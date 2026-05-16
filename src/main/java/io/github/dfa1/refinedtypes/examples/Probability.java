package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedFloat;

/// A probability in the closed interval [0, 1].
///
/// Refining the type rules out two common bug classes at the boundary:
/// accidental percentages (`0..100`) being passed where probabilities
/// were expected, and out-of-range outputs from a buggy estimator silently
/// propagating into downstream multiplications.
public value class Probability implements RefinedFloat<Probability> {

    public static final Probability ZERO = new Probability(0f);
    public static final Probability ONE  = new Probability(1f);

    private final float value;

    public Probability(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value) || value < 0f || value > 1f) {
            throw new IllegalArgumentException("probability must be finite and in [0, 1]: " + value);
        }
        this.value = value;
    }

    @Override
    public float value() {
        return value;
    }

    /// Complementary probability `1 - p`.
    public Probability complement() {
        return new Probability(1f - value);
    }

    /// Independent-event conjunction: `this AND other`.
    public Probability and(Probability other) {
        return new Probability(this.value * other.value);
    }

    /// Independent-event disjunction: `this OR other`.
    public Probability or(Probability other) {
        float p = this.value + other.value - this.value * other.value;
        if (p > 1f) {
            p = 1f;
        }
        return new Probability(p);
    }

    @Override
    public String toString() {
        return "Probability(" + value + ")";
    }
}
