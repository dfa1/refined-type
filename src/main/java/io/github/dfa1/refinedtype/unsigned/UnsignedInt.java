package io.github.dfa1.refinedtype.unsigned;

import io.github.dfa1.refinedtype.RefinedInt;

/// Unsigned 32-bit integer: range [0, 4_294_967_295].
/// Stored as raw int bits; unsigned semantics via Integer.toUnsignedLong / compareUnsigned.
///
/// Implements {@link RefinedInt}: `value()` returns the raw 32-bit pattern as a signed
/// int (so values above 2^31 - 1 read as negative). For the unsigned magnitude as
/// `long`, use {@link #asLong()}.
public value class UnsignedInt implements RefinedInt<UnsignedInt> {

    public static final long MIN_VALUE = 0L;
    public static final long MAX_VALUE = 0xFFFFFFFFL; // 4_294_967_295

    private final int bits;

    private UnsignedInt(long value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("out of unsigned int range [0, 4294967295]: " + value);
        }
        this.bits = (int) value;
    }

    public static UnsignedInt of(long value) {
        return new UnsignedInt(value);
    }

    /// Raw 32-bit pattern, signed interpretation. Negative for values > 2^31 - 1.
    /// Use {@link #asLong()} for the unsigned magnitude as long.
    @Override
    public int value() {
        return bits;
    }

    /// Unsigned value as long (always non-negative, in [0, 4_294_967_295]).
    public long asLong() {
        return Integer.toUnsignedLong(bits);
    }

    /// Parse an unsigned decimal string in [0, 4_294_967_295].
    public static UnsignedInt fromString(String s) {
        return new UnsignedInt(Long.parseLong(s));
    }

    /// Widen to {@link UnsignedLong}.
    ///
    /// Cross-type arithmetic is not provided as overloads — see
    /// {@link UnsignedShort#toUnsignedInt()} for the full rationale.
    /// Widen to the desired precision first, then use same-type arithmetic:
    ///
    /// ```java
    /// UnsignedInt  i = new UnsignedInt(4_000_000_000L);
    /// UnsignedLong l = UnsignedLong.fromString("9999999999999999999");
    /// UnsignedLong result = i.toUnsignedLong().add(l);
    /// ```
    public UnsignedLong toUnsignedLong() {
        return UnsignedLong.of(Integer.toUnsignedLong(bits));
    }

    /// Wraps mod 2^32.
    public UnsignedInt add(UnsignedInt other) {
        return ofBits(this.bits + other.bits);
    }

    /// Wraps mod 2^32.
    public UnsignedInt subtract(UnsignedInt other) {
        return ofBits(this.bits - other.bits);
    }

    /// Lower 32 bits of product; wraps mod 2^32.
    public UnsignedInt multiply(UnsignedInt other) {
        return ofBits(this.bits * other.bits);
    }

    /// @throws ArithmeticException if other is zero
    public UnsignedInt divide(UnsignedInt other) {
        return ofBits(Integer.divideUnsigned(this.bits, other.bits));
    }

    /// @throws ArithmeticException if other is zero
    public UnsignedInt remainder(UnsignedInt other) {
        return ofBits(Integer.remainderUnsigned(this.bits, other.bits));
    }

    /// Unsigned ordering — overrides {@link RefinedInt}'s signed default.
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
