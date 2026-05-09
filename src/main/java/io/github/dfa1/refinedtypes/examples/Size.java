package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedLong;

/**
 * A size in bytes — non-negative {@code long}.
 *
 * <p>Models file sizes, buffer capacities, allocation budgets — anything
 * counted in bytes whose range exceeds the 2 GB ceiling of {@code int}.
 *
 * <p>Multiplier helpers use the binary base ({@code 1024}) — same convention
 * as the JDK ({@code Files.size}, {@code Runtime.totalMemory}). For decimal
 * bases use {@link #ofBytes(long)} directly.
 */
public value class Size implements RefinedLong {

    public static final long KB = 1024L;
    public static final long MB = 1024L * KB;
    public static final long GB = 1024L * MB;
    public static final long TB = 1024L * GB;

    public static final Size ZERO = new Size(0L);

    private final long bytes;

    public Size(long bytes) {
        if (bytes < 0L) {
            throw new IllegalArgumentException("size must be non-negative: " + bytes);
        }
        this.bytes = bytes;
    }

    public static Size ofBytes(long n)     { return new Size(n); }
    public static Size ofKilobytes(long n) { return new Size(Math.multiplyExact(n, KB)); }
    public static Size ofMegabytes(long n) { return new Size(Math.multiplyExact(n, MB)); }
    public static Size ofGigabytes(long n) { return new Size(Math.multiplyExact(n, GB)); }
    public static Size ofTerabytes(long n) { return new Size(Math.multiplyExact(n, TB)); }

    @Override
    public long value() {
        return bytes;
    }

    public long toBytes()     { return bytes; }
    public long toKilobytes() { return bytes / KB; }
    public long toMegabytes() { return bytes / MB; }
    public long toGigabytes() { return bytes / GB; }
    public long toTerabytes() { return bytes / TB; }

    /** @throws ArithmeticException on long overflow */
    public Size plus(Size other) {
        return new Size(Math.addExact(this.bytes, other.bytes));
    }

    /** @throws IllegalArgumentException if {@code other > this} (size cannot go negative) */
    public Size minus(Size other) {
        return new Size(this.bytes - other.bytes);
    }

    public boolean isZero() {
        return bytes == 0L;
    }

    /** Human-readable form: bytes for &lt;1 KiB, then KiB, MiB, GiB, TiB with one decimal. */
    @Override
    public String toString() {
        if (bytes < KB) return "Size(" + bytes + " B)";
        if (bytes < MB) return "Size(" + format(bytes, KB) + " KiB)";
        if (bytes < GB) return "Size(" + format(bytes, MB) + " MiB)";
        if (bytes < TB) return "Size(" + format(bytes, GB) + " GiB)";
        return "Size(" + format(bytes, TB) + " TiB)";
    }

    private static String format(long bytes, long unit) {
        long whole = bytes / unit;
        long tenths = (bytes % unit) * 10L / unit;
        return whole + "." + tenths;
    }
}
