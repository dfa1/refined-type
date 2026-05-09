package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedInt;

/**
 * TCP/UDP port number — IANA range [0, 65535].
 *
 * <p>Port 0 is permitted: the operating system interprets it as
 * "assign an ephemeral port" when binding a socket.
 *
 * <p>Refined-typing the port closes a common SSRF / mis-routing class
 * of bug where a plain {@code int} carries an out-of-range value all
 * the way down to the syscall, which then fails with an opaque error.
 */
public value class Port implements RefinedInt {

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 65_535;

    private final int value;

    public Port(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("port out of range [0, 65535]: " + value);
        }
        this.value = value;
    }

    public static Port fromString(String s) {
        return new Port(Integer.parseInt(s));
    }

    @Override
    public int value() {
        return value;
    }

    /** True if {@code value} is a well-known / system port (0–1023). */
    public boolean isWellKnown() {
        return value <= 1023;
    }

    /** True if {@code value} is an ephemeral port per IANA (49152–65535). */
    public boolean isEphemeral() {
        return value >= 49_152;
    }

    @Override
    public String toString() {
        return "Port(" + value + ")";
    }
}
