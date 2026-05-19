package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import java.util.regex.Pattern;

/// Legal Entity Identifier (ISO 17442) — 20-character alphanumeric code
/// that uniquely identifies a legal entity participating in financial markets.
///
/// Layout:
///
/// ```
/// PPPP EEEEEEEEEEEEEE CC
/// ```
///
/// - `PPPP`           — 4-char LOU (Local Operating Unit) prefix, letters or digits.
/// - `EEEEEEEEEEEEEE` — 14-char entity-specific part, letters or digits.
/// - `CC`             — 2-digit check digits (ISO 7064 MOD 97-10, same algorithm as IBAN).
///
/// Two factory methods are provided:
///
/// - {@link #of(String)} — parse and validate a complete 20-character LEI (structure + MOD 97-10 checksum).
/// - {@link #ofPrefix(String)} — compute valid check digits from an 18-character prefix
///   and return a guaranteed-valid LEI.
///
/// Examples: `7H6GLXDRUGQFU57RNE97` (Deutsche Bank),
/// `HWUPKR0MPOU8FGXBT394` (JPMorgan Chase).
public value class Lei implements RefinedString<Lei> {

    private static final int LENGTH    = 20;
    private static final int PREFIX_LENGTH = 18;
    private static final Pattern PATTERN        = Pattern.compile("^[A-Z0-9]{18}[0-9]{2}$");
    private static final Pattern PREFIX_PATTERN = Pattern.compile("^[A-Z0-9]{18}$");

    private final String value;

    private Lei(String value) {
        this.value = value;
    }

    /// Parse and validate a complete 20-character LEI (structure + MOD 97-10 checksum).
    public static Lei of(String value) {
        if (value == null || value.length() != LENGTH) {
            throw new IllegalArgumentException("LEI must be exactly 20 characters: " + value);
        }
        String upper = value.toUpperCase();
        if (!PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException(
                    "LEI must match ^[A-Z0-9]{18}[0-9]{2}$ (18 alphanumeric + 2 numeric check digits): " + value);
        }
        if (mod97(upper) != 1) {
            throw new IllegalArgumentException("LEI checksum invalid (MOD 97-10): " + value);
        }
        return new Lei(upper);
    }

    /// Build a valid LEI from an 18-character alphanumeric prefix by computing
    /// the ISO 7064 MOD 97-10 check digits.
    ///
    /// Algorithm:
    ///
    /// 1. Append `"00"` to the prefix.
    /// 2. Replace each letter with its numeric value (`A`=10, …, `Z`=35).
    /// 3. Compute the resulting integer string modulo 97.
    /// 4. Check digits = `(98 − result) mod 97`, zero-padded to 2 digits.
    public static Lei ofPrefix(String prefix18) {
        if (prefix18 == null || prefix18.length() != PREFIX_LENGTH) {
            throw new IllegalArgumentException("LEI prefix must be exactly 18 characters: " + prefix18);
        }
        String upper = prefix18.toUpperCase();
        if (!PREFIX_PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException(
                    "LEI prefix must be alphanumeric (A-Z, 0-9): " + prefix18);
        }
        int check = 98 - mod97(upper + "00");
        return new Lei(upper + String.format("%02d", check));
    }

    @Override
    public String value() {
        return value;
    }

    /// The 4-character LOU (Local Operating Unit) prefix.
    public String lou() {
        return value.substring(0, 4);
    }

    /// The 14-character entity-specific part (without LOU prefix or check digits).
    public String entityCode() {
        return value.substring(4, 18);
    }

    /// Two-digit MOD 97-10 check digits as a string (positions 19–20), e.g. `"97"` or `"01"`.
    ///
    /// Returns `String` rather than `int` to preserve zero-padding (e.g. `"01"`, not `1`)
    /// and for consistency with {@link Iban#checkDigits()}.
    public String checkDigits() {
        return value.substring(18);
    }

    private static int mod97(String s) {
        // Process in chunks to avoid BigInteger; int is wide enough for 9-char blocks.
        StringBuilder numeric = new StringBuilder(s.length() * 2);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                numeric.append(c);
            } else {
                numeric.append(c - 'A' + 10);
            }
        }
        int remainder = 0;
        for (int i = 0; i < numeric.length(); i++) {
            remainder = (remainder * 10 + (numeric.charAt(i) - '0')) % 97;
        }
        return remainder;
    }

    @Override
    public String toString() {
        return "Lei(" + value + ")";
    }
}
