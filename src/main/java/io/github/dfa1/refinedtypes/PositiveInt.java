package io.github.dfa1.refinedtypes;

public value class PositiveInt implements RefinedInt {

    private final int value;

    public PositiveInt(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("not positive: " + value);
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
