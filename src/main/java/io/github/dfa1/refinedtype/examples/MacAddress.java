package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;

import java.util.Locale;
import java.util.regex.Pattern;

/// IEEE 802 MAC address, stored in canonical lowercase colon-separated form
/// (`aa:bb:cc:dd:ee:ff`).
///
/// Accepted input formats:
///
/// - Colon-separated: `aa:bb:cc:dd:ee:ff` or `AA:BB:CC:DD:EE:FF`
/// - Hyphen-separated: `aa-bb-cc-dd-ee-ff`
/// - No separator: `aabbccddeeff`
///
/// Mixed separators (`aa:bb-cc:dd:ee:ff`) are rejected.
///
/// The constructor normalizes to lowercase with colons — input case and
/// separator style do not affect equality.
public value class MacAddress implements RefinedString<MacAddress> {

    private static final Pattern PATTERN = Pattern.compile(
            "[0-9a-fA-F]{2}(:[0-9a-fA-F]{2}){5}" +
            "|[0-9a-fA-F]{2}(-[0-9a-fA-F]{2}){5}" +
            "|[0-9a-fA-F]{12}");

    /// Broadcast address (`ff:ff:ff:ff:ff:ff`).
    public static final MacAddress BROADCAST = new MacAddress("ff:ff:ff:ff:ff:ff");

    private final String value;

    private MacAddress(String value) {
        if (value == null) {
            throw new IllegalArgumentException("MAC address must not be null");
        }
        if (value.length() != 17 && value.length() != 12) {
            throw new IllegalArgumentException("invalid MAC address: " + value);
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("invalid MAC address: " + value);
        }
        String hex = value.replace(":", "").replace("-", "").toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder(17);
        for (int i = 0; i < 12; i += 2) {
            if (i > 0) sb.append(':');
            sb.append(hex, i, i + 2);
        }
        this.value = sb.toString();
    }

    public static MacAddress of(String value) {
        return new MacAddress(value);
    }

    /// Returns the canonical lowercase colon-separated form.
    @Override
    public String value() {
        return value;
    }

    /// Returns the six octets as integers in `[0, 255]`.
    public int[] octets() {
        int[] result = new int[6];
        for (int i = 0; i < 6; i++) {
            result[i] = Integer.parseInt(value.substring(i * 3, i * 3 + 2), 16);
        }
        return result;
    }

    /// Returns `true` if the multicast bit (LSB of first octet) is set.
    ///
    /// The broadcast address is a special case of multicast and also returns `true`.
    public boolean isMulticast() {
        return (octets()[0] & 0x01) == 1;
    }

    /// Returns `true` if the locally-administered bit (bit 1 of first octet) is set.
    ///
    /// Locally-administered addresses are assigned by a network administrator
    /// rather than the device manufacturer.
    public boolean isLocallyAdministered() {
        return (octets()[0] & 0x02) == 2;
    }

    /// Returns `true` for the broadcast address `ff:ff:ff:ff:ff:ff`.
    public boolean isBroadcast() {
        return value.equals("ff:ff:ff:ff:ff:ff");
    }

    @Override
    public String toString() {
        return "MacAddress(" + value + ")";
    }
}
