package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedString;

/**
 * DNS hostname — RFC 1123 preferred name syntax.
 *
 * <p>Constraints enforced at construction:
 * <ul>
 *   <li>total length 1–253</li>
 *   <li>one or more labels separated by {@code '.'}</li>
 *   <li>each label 1–63 characters</li>
 *   <li>each label uses {@code [A-Za-z0-9-]}, no leading or trailing {@code '-'}</li>
 * </ul>
 *
 * <p>The value is normalised to lower-case — DNS lookups are
 * case-insensitive and downstream code should not have to remember.
 *
 * <p>This type does <em>not</em> resolve the name and does not accept
 * IPv4 / IPv6 literals. Use a dedicated {@code IpAddress} type if you
 * need either.
 */
public value class HostName implements RefinedString {

    public static final int MAX_LENGTH = 253;
    public static final int MAX_LABEL_LENGTH = 63;

    private final String value;

    public HostName(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("hostname must not be empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("hostname exceeds 253 characters: " + value.length());
        }
        String lower = value.toLowerCase();
        int labelStart = 0;
        for (int i = 0; i <= lower.length(); i++) {
            boolean atDot = (i == lower.length()) || lower.charAt(i) == '.';
            if (!atDot) {
                if (!isLabelChar(lower.charAt(i))) {
                    throw new IllegalArgumentException("hostname contains invalid character: " + value);
                }
                continue;
            }
            int labelLen = i - labelStart;
            if (labelLen == 0) {
                throw new IllegalArgumentException("hostname has empty label: " + value);
            }
            if (labelLen > MAX_LABEL_LENGTH) {
                throw new IllegalArgumentException("hostname label exceeds 63 characters: " + value);
            }
            if (lower.charAt(labelStart) == '-' || lower.charAt(i - 1) == '-') {
                throw new IllegalArgumentException("hostname label has leading or trailing hyphen: " + value);
            }
            labelStart = i + 1;
        }
        this.value = lower;
    }

    private static boolean isLabelChar(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9')
                || c == '-';
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "HostName(" + value + ")";
    }
}
