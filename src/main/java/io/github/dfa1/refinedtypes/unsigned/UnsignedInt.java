package io.github.dfa1.refinedtypes.unsigned;

/**
 * Unsigned 32-bit integer: range [0, 4_294_967_295].
 * Stored as raw int bits; unsigned semantics via Integer.toUnsignedLong / compareUnsigned.
 */
public value class UnsignedInt implements Comparable<UnsignedInt> {

    public static final long MIN_VALUE = 0L;
    public static final long MAX_VALUE = 0xFFFFFFFFL; // 4_294_967_295

    private final int bits;

    public UnsignedInt(long value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("out of unsigned int range [0, 4294967295]: " + value);
        }
        this.bits = (int) value;
    }

    /** Unsigned value as long (always non-negative). */
    public long value() {
        return Integer.toUnsignedLong(bits);
    }

    /** Raw bit pattern — use only when passing to Integer.xxxUnsigned methods. */
    public int rawBits() {
        return bits;
    }

    /** Wraps mod 2^32. */
    public UnsignedInt add(UnsignedInt other) {
        return ofBits(this.bits + other.bits);
    }

    /** Wraps mod 2^32. */
    public UnsignedInt subtract(UnsignedInt other) {
        return ofBits(this.bits - other.bits);
    }

    /** Lower 32 bits of product; wraps mod 2^32. */
    public UnsignedInt multiply(UnsignedInt other) {
        return ofBits(this.bits * other.bits);
    }

    /** @throws ArithmeticException if other is zero */
    public UnsignedInt divide(UnsignedInt other) {
        return ofBits(Integer.divideUnsigned(this.bits, other.bits));
    }

    /** @throws ArithmeticException if other is zero */
    public UnsignedInt remainder(UnsignedInt other) {
        return ofBits(Integer.remainderUnsigned(this.bits, other.bits));
    }

    @Override
    public int compareTo(UnsignedInt that) {
        return Integer.compareUnsigned(this.bits, that.bits);
    }

    @Override
    public String toString() {
        return "UnsignedInt(" + Integer.toUnsignedString(bits) + ")";
    }

    // Integer.toUnsignedLong always returns a value in [0, MAX_VALUE] — constructor never throws.
    private static UnsignedInt ofBits(int bits) {
        return new UnsignedInt(Integer.toUnsignedLong(bits));
    }
}
