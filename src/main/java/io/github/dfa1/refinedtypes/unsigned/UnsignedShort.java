package io.github.dfa1.refinedtypes.unsigned;

/**
 * Unsigned 16-bit integer: range [0, 65_535].
 * Stored as raw short bits; unsigned semantics via Short.toUnsignedInt / compareUnsigned.
 */
public value class UnsignedShort implements Comparable<UnsignedShort> {

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 0xFFFF; // 65_535

    private final short bits;

    public UnsignedShort(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("out of unsigned short range [0, 65535]: " + value);
        }
        this.bits = (short) value;
    }

    /** Unsigned value as int (always non-negative). */
    public int value() {
        return Short.toUnsignedInt(bits);
    }

    /** Raw bit pattern — use only when passing to Short.xxxUnsigned / Integer.xxxUnsigned methods. */
    public short rawBits() {
        return bits;
    }

    @Override
    public int compareTo(UnsignedShort that) {
        return Integer.compareUnsigned(Short.toUnsignedInt(this.bits), Short.toUnsignedInt(that.bits));
    }

    @Override
    public String toString() {
        return "UnsignedShort(" + Short.toUnsignedInt(bits) + ")";
    }
}
