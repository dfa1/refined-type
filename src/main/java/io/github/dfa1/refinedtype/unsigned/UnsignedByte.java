package io.github.dfa1.refinedtype.unsigned;

/// Unsigned 8-bit integer: range [0, 255].
/// Stored as raw byte bits; unsigned semantics via Byte.toUnsignedInt.
public value class UnsignedByte implements Comparable<UnsignedByte> {

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 0xFF; // 255

    private final byte bits;

    public UnsignedByte(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("out of unsigned byte range [0, 255]: " + value);
        }
        this.bits = (byte) value;
    }

    /// Parse an unsigned decimal string in [0, 255].
    public static UnsignedByte fromString(String s) {
        return new UnsignedByte(Integer.parseInt(s));
    }

    /// Unsigned value as int (always non-negative).
    public int value() {
        return Byte.toUnsignedInt(bits);
    }

    /// Raw bit pattern — use only when passing to Byte.toUnsignedInt / Byte.toUnsignedLong.
    public byte rawBits() {
        return bits;
    }

    /// Wraps mod 2^8.
    public UnsignedByte add(UnsignedByte other) {
        return ofBits(Byte.toUnsignedInt(this.bits) + Byte.toUnsignedInt(other.bits));
    }

    /// Wraps mod 2^8.
    public UnsignedByte subtract(UnsignedByte other) {
        return ofBits(Byte.toUnsignedInt(this.bits) - Byte.toUnsignedInt(other.bits));
    }

    /// Lower 8 bits of product; wraps mod 2^8.
    public UnsignedByte multiply(UnsignedByte other) {
        return ofBits(Byte.toUnsignedInt(this.bits) * Byte.toUnsignedInt(other.bits));
    }

    /// @throws ArithmeticException if other is zero
    public UnsignedByte divide(UnsignedByte other) {
        return new UnsignedByte(Byte.toUnsignedInt(this.bits) / Byte.toUnsignedInt(other.bits));
    }

    /// @throws ArithmeticException if other is zero
    public UnsignedByte remainder(UnsignedByte other) {
        return new UnsignedByte(Byte.toUnsignedInt(this.bits) % Byte.toUnsignedInt(other.bits));
    }

    /// Widen to {@link UnsignedShort}.
    ///
    /// Cross-type arithmetic is not provided as overloads — see
    /// {@link UnsignedShort#toUnsignedInt()} for the rationale. Widen explicitly first:
    ///
    /// ```java
    /// UnsignedByte  b = new UnsignedByte(200);
    /// UnsignedShort s = new UnsignedShort(1000);
    /// UnsignedShort result = b.toUnsignedShort().add(s);
    /// ```
    public UnsignedShort toUnsignedShort() {
        return new UnsignedShort(Byte.toUnsignedInt(bits));
    }

    /// Widen to {@link UnsignedInt}. See {@link #toUnsignedShort()} for rationale.
    public UnsignedInt toUnsignedInt() {
        return new UnsignedInt(Byte.toUnsignedInt(bits));
    }

    /// Widen to {@link UnsignedLong}. See {@link #toUnsignedShort()} for rationale.
    public UnsignedLong toUnsignedLong() {
        return new UnsignedLong(Byte.toUnsignedLong(bits));
    }

    @Override
    public int compareTo(UnsignedByte that) {
        return Integer.compareUnsigned(Byte.toUnsignedInt(this.bits), Byte.toUnsignedInt(that.bits));
    }

    @Override
    public String toString() {
        return "UnsignedByte(" + Byte.toUnsignedInt(bits) + ")";
    }

    // Masks to lower 8 bits — constructor never throws for values in [0, 0xFF].
    private static UnsignedByte ofBits(int bits) {
        return new UnsignedByte(bits & 0xFF);
    }
}
