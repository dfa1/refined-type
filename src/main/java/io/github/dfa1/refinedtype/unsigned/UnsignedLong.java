package io.github.dfa1.refinedtype.unsigned;

/// Unsigned 64-bit integer: range [0, 18_446_744_073_709_551_615].
/// Stored as raw long bits; unsigned semantics via Long.toUnsignedString / compareUnsigned.
///
/// Every long bit pattern is a valid UnsignedLong — the constructor never throws.
/// Because 2^64-1 exceeds Long.MAX_VALUE there is no value() returning a primitive;
/// use rawBits() with Long.toUnsignedString / Long.compareUnsigned etc.
public value class UnsignedLong implements Comparable<UnsignedLong> {

    public static final UnsignedLong ZERO = new UnsignedLong(0L);
    public static final UnsignedLong MAX  = new UnsignedLong(-1L); // all bits set = 2^64 - 1

    private final long bits;

    private UnsignedLong(long bits) {
        this.bits = bits;
    }

    public static UnsignedLong of(long bits) {
        return new UnsignedLong(bits);
    }

    /// Parse an unsigned decimal string, accepting values up to 18_446_744_073_709_551_615.
    public static UnsignedLong fromString(String s) {
        return new UnsignedLong(Long.parseUnsignedLong(s));
    }

    /// Raw bit pattern — use with Long.toUnsignedString / Long.xxxUnsigned methods.
    public long rawBits() {
        return bits;
    }

    /// Wraps mod 2^64.
    public UnsignedLong add(UnsignedLong other) {
        return new UnsignedLong(this.bits + other.bits);
    }

    /// Wraps mod 2^64.
    public UnsignedLong subtract(UnsignedLong other) {
        return new UnsignedLong(this.bits - other.bits);
    }

    /// Lower 64 bits of product; wraps mod 2^64.
    public UnsignedLong multiply(UnsignedLong other) {
        return new UnsignedLong(this.bits * other.bits);
    }

    /// @throws ArithmeticException if other is zero
    public UnsignedLong divide(UnsignedLong other) {
        return new UnsignedLong(Long.divideUnsigned(this.bits, other.bits));
    }

    /// @throws ArithmeticException if other is zero
    public UnsignedLong remainder(UnsignedLong other) {
        return new UnsignedLong(Long.remainderUnsigned(this.bits, other.bits));
    }

    @Override
    public int compareTo(UnsignedLong that) {
        return Long.compareUnsigned(this.bits, that.bits);
    }

    @Override
    public String toString() {
        return "UnsignedLong(" + Long.toUnsignedString(bits) + ")";
    }
}
