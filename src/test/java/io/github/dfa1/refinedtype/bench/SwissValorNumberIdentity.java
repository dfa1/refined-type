package io.github.dfa1.refinedtype.bench;

/// Identity-class mirror of {@link io.github.dfa1.refinedtype.examples.SwissValorNumber}.
/// Used only in benchmarks to isolate value-class inlining overhead vs heap allocation.
class SwissValorNumberIdentity {

    static final int MIN_VALUE = 1;
    static final int MAX_VALUE = 999_999_999;

    private final int value;

    SwissValorNumberIdentity(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Swiss valor must be in [1, 999_999_999]: " + value);
        }
        this.value = value;
    }

    int value() {
        return value;
    }
}
