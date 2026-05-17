package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedLong;

/// A size in bytes — non-negative `long`.
///
/// Models file sizes, buffer capacities, allocation budgets — anything
/// counted in bytes whose range exceeds the 2 GB ceiling of `int`.
///
/// Multipliers use the binary base (`1024`) — same convention as the JDK
/// (`Files.size`, `Runtime.totalMemory`). Construct with {@link #of(long, Unit)}
/// and convert back via {@link #to(Unit)}.
public value class Size implements RefinedLong<Size> {

    /// Binary multipliers. {@code bytes} is the number of raw bytes in
    /// one unit (e.g. {@code Unit.MB.bytes == 1_048_576}).
    public enum Unit {
        B (1L),
        KB(1024L),
        MB(1024L * 1024L),
        GB(1024L * 1024L * 1024L),
        TB(1024L * 1024L * 1024L * 1024L);

        public final long bytes;

        Unit(long bytes) {
            this.bytes = bytes;
        }
    }

    public static final Size ZERO = new Size(0L);

    private final long bytes;

    public Size(long bytes) {
        if (bytes < 0L) {
            throw new IllegalArgumentException("size must be non-negative: " + bytes);
        }
        this.bytes = bytes;
    }

    /// Construct from a count of the given unit.
    /// @throws ArithmeticException on long overflow
    public static Size of(long n, Unit unit) {
        return new Size(Math.multiplyExact(n, unit.bytes));
    }

    @Override
    public long value() {
        return bytes;
    }

    /// Convert to a whole number of the given unit (truncating).
    public long to(Unit unit) {
        return bytes / unit.bytes;
    }

    /// @throws ArithmeticException on long overflow
    public Size plus(Size other) {
        return new Size(Math.addExact(this.bytes, other.bytes));
    }

    /// @throws IllegalArgumentException if `other > this` (size cannot go negative)
    public Size minus(Size other) {
        return new Size(this.bytes - other.bytes);
    }

    public boolean isZero() {
        return bytes == 0L;
    }

    /// Human-readable form: bytes for <1 KiB, then KiB, MiB, GiB, TiB with one decimal.
    @Override
    public String toString() {
        if (bytes < Unit.KB.bytes) return "Size(" + bytes + " B)";
        if (bytes < Unit.MB.bytes) return "Size(" + format(bytes, Unit.KB.bytes) + " KiB)";
        if (bytes < Unit.GB.bytes) return "Size(" + format(bytes, Unit.MB.bytes) + " MiB)";
        if (bytes < Unit.TB.bytes) return "Size(" + format(bytes, Unit.GB.bytes) + " GiB)";
        return "Size(" + format(bytes, Unit.TB.bytes) + " TiB)";
    }

    private static String format(long bytes, long unit) {
        long whole = bytes / unit;
        long tenths = (bytes % unit) * 10L / unit;
        return whole + "." + tenths;
    }
}
