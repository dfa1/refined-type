package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedString;

/**
 * DNS hostname — RFC 1123 preferred name syntax, with SSRF guards.
 *
 * <p>Constraints enforced at construction:
 * <ul>
 *   <li>total length 1–253</li>
 *   <li>one or more labels separated by {@code '.'}</li>
 *   <li>each label 1–63 characters</li>
 *   <li>each label uses {@code [A-Za-z0-9-]}, no leading or trailing {@code '-'}</li>
 * </ul>
 *
 * <p><b>SSRF guards</b> (Server-Side Request Forgery):
 * <ul>
 *   <li>{@code localhost} and common loopback aliases are rejected by name.</li>
 *   <li>IPv4 literals whose first two octets map to a private, loopback, or
 *       link-local range are rejected: {@code 127.x.x.x}, {@code 10.x.x.x},
 *       {@code 172.16–31.x.x}, {@code 192.168.x.x}, {@code 169.254.x.x}
 *       (includes the AWS instance-metadata endpoint {@code 169.254.169.254}).</li>
 * </ul>
 *
 * <p>The value is normalised to lower-case — DNS lookups are case-insensitive.
 *
 * <p>This type does <em>not</em> perform DNS resolution and does not accept
 * IPv6 literals. For numeric addresses use a dedicated {@code IpAddress} type.
 */
public value class HostName implements RefinedString {

    public static final int MAX_LENGTH       = 253;
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

        // SSRF guard — loopback aliases
        if (lower.equals("localhost") || lower.equals("localhost.localdomain")
                || lower.equals("ip6-localhost") || lower.equals("ip6-loopback")) {
            throw new IllegalArgumentException("hostname is a loopback alias (SSRF guard): " + value);
        }

        // Format validation
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

        // SSRF guard — private/loopback IPv4 literals (no DNS resolution)
        if (isBlockedIpv4(lower)) {
            throw new IllegalArgumentException("IPv4 address in private or loopback range (SSRF guard): " + value);
        }

        this.value = lower;
    }

    private static boolean isLabelChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '-';
    }

    /**
     * Returns true if {@code host} is an IPv4 literal in a blocked range.
     * Only considers strings composed of exactly 4 all-digit labels; hostnames
     * with letters are skipped to avoid false positives (e.g. "10.example.com").
     */
    private static boolean isBlockedIpv4(String host) {
        String[] parts = host.split("\\.", -1);
        if (parts.length != 4) return false;
        int[] oct = new int[4];
        for (int i = 0; i < 4; i++) {
            if (parts[i].isEmpty()) return false;
            for (char c : parts[i].toCharArray()) {
                if (c < '0' || c > '9') return false; // has letter — not a pure IPv4
            }
            oct[i] = Integer.parseInt(parts[i]);
            if (oct[i] > 255) return false;
        }
        int a = oct[0], b = oct[1];
        return a == 127                           // 127.x.x.x  loopback
            || a == 10                            // 10.x.x.x   RFC 1918 class A
            || (a == 172 && b >= 16 && b <= 31)  // 172.16-31.x RFC 1918 class B
            || (a == 192 && b == 168)            // 192.168.x.x RFC 1918 class C
            || (a == 169 && b == 254)            // 169.254.x.x link-local / AWS metadata
            || a == 0;                            // 0.x.x.x     unspecified
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
