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
    public int toInt() {
        return Short.toUnsignedInt(bits);
    }

    /** Raw bit pattern — use only when passing to Short.xxxUnsigned / Integer.xxxUnsigned methods. */
    public short rawBits() {
        return bits;
    }

    /**
     * Widen to {@link UnsignedInt}.
     *
     * <p>Cross-type arithmetic (e.g. {@code UnsignedShort + UnsignedInt}) is intentionally
     * not provided as overloads: supporting every combination across three types would require
     * O(types²·ops) methods and force callers to reason about which overload wins.
     * Instead, widen explicitly to the desired precision first — the same contract the JDK
     * uses with {@link Short#toUnsignedInt} — then call the same-type arithmetic method.
     *
     * <pre>{@code
     * UnsignedShort s = new UnsignedShort(1000);
     * UnsignedInt   i = new UnsignedInt(3_000_000_000L);
     * UnsignedInt result = s.toUnsignedInt().add(i);
     * }</pre>
     */
    public UnsignedInt toUnsignedInt() {
        return new UnsignedInt(Short.toUnsignedInt(bits));
    }

    /**
     * Widen to {@link UnsignedLong}.
     *
     * <p>See {@link #toUnsignedInt()} for the rationale behind explicit widening.
     */
    public UnsignedLong toUnsignedLong() {
        return new UnsignedLong(Short.toUnsignedLong(bits));
    }

    /** Wraps mod 2^16. */
    public UnsignedShort add(UnsignedShort other) {
        return ofBits(Short.toUnsignedInt(this.bits) + Short.toUnsignedInt(other.bits));
    }

    /** Wraps mod 2^16. */
    public UnsignedShort subtract(UnsignedShort other) {
        return ofBits(Short.toUnsignedInt(this.bits) - Short.toUnsignedInt(other.bits));
    }

    /** Lower 16 bits of product; wraps mod 2^16. */
    public UnsignedShort multiply(UnsignedShort other) {
        return ofBits(Short.toUnsignedInt(this.bits) * Short.toUnsignedInt(other.bits));
    }

    /** @throws ArithmeticException if other is zero */
    public UnsignedShort divide(UnsignedShort other) {
        return new UnsignedShort(Short.toUnsignedInt(this.bits) / Short.toUnsignedInt(other.bits));
    }

    /** @throws ArithmeticException if other is zero */
    public UnsignedShort remainder(UnsignedShort other) {
        return new UnsignedShort(Short.toUnsignedInt(this.bits) % Short.toUnsignedInt(other.bits));
    }

    @Override
    public int compareTo(UnsignedShort that) {
        return Integer.compareUnsigned(Short.toUnsignedInt(this.bits), Short.toUnsignedInt(that.bits));
    }

    @Override
    public String toString() {
        return "UnsignedShort(" + Short.toUnsignedInt(bits) + ")";
    }

    // Masks to lower 16 bits — constructor never throws for values in [0, 0xFFFF].
    private static UnsignedShort ofBits(int bits) {
        return new UnsignedShort(bits & 0xFFFF);
    }
}
