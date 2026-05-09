package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedFloat;

/**
 * A percentage in the closed interval [0, 100].
 *
 * <p>Use when an API contract is stated in human-readable percent —
 * UI inputs, JSON payloads, configuration. For probability arithmetic
 * use {@link Probability} instead, which lives in [0, 1] and avoids
 * the implicit "/100" that callers otherwise sprinkle through code.
 */
public value class Percentage implements RefinedFloat {

    public static final Percentage ZERO     = new Percentage(0f);
    public static final Percentage HUNDRED  = new Percentage(100f);

    private final float value;

    public Percentage(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value) || value < 0f || value > 100f) {
            throw new IllegalArgumentException("percentage must be finite and in [0, 100]: " + value);
        }
        this.value = value;
    }

    /** Convert a probability in [0, 1] to a percentage in [0, 100]. */
    public static Percentage ofProbability(Probability p) {
        return new Percentage(p.value() * 100f);
    }

    @Override
    public float value() {
        return value;
    }

    /** Convert to a probability in [0, 1]. */
    public Probability toProbability() {
        return new Probability(value / 100f);
    }

    @Override
    public String toString() {
        return "Percentage(" + value + "%)";
    }
}
