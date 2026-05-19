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
/// Two entry points:
///
/// - {@link #Lei(String)} — parse an already-complete 20-character LEI (shape check only;
///   checksum is **not** verified, matching the approach of {@link Isin}).
/// - {@link #of(String)} — compute valid check digits from an 18-character prefix and return
///   a guaranteed-valid LEI.
///
/// Examples: `7H6GLXDRUGQFU57RNE97` (Deutsche Bank),
/// `HWUPKR0MPOU8FGXBT394` (JPMorgan Chase).
public value class Lei implements RefinedString<Lei> {

    private static final int LENGTH    = 20;
    private static final int PREFIX_LENGTH = 18;
    private static final Pattern PATTERN        = Pattern.compile("^[A-Z0-9]{18}[0-9]{2}$");
    private static final Pattern PREFIX_PATTERN = Pattern.compile("^[A-Z0-9]{18}$");

    private final String value;

    public Lei(String value) {
        if (value == null || value.length() != LENGTH) {
            throw new IllegalArgumentException("LEI must be exactly 20 characters: " + value);
        }
        String upper = value.toUpperCase();
        if (!PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException(
                    "LEI must match ^[A-Z0-9]{18}[0-9]{2}$ (18 alphanumeric + 2 numeric check digits): " + value);
        }
        this.value = upper;
    }

    private Lei(String prefix18, int checkDigits) {
        this.value = prefix18 + String.format("%02d", checkDigits);
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
    public static Lei of(String prefix18) {
        if (prefix18 == null || prefix18.length() != PREFIX_LENGTH) {
            throw new IllegalArgumentException("LEI prefix must be exactly 18 characters: " + prefix18);
        }
        String upper = prefix18.toUpperCase();
        if (!PREFIX_PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException(
                    "LEI prefix must be alphanumeric (A-Z, 0-9): " + prefix18);
        }
        int check = 98 - mod97(upper + "00");
        return new Lei(upper, check);
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

    /// The 2-digit check digits as an integer in [1, 97].
    public int checkDigits() {
        return Integer.parseInt(value.substring(18));
    }

    /// Returns `true` if the ISO 7064 MOD 97-10 checksum is valid.
    ///
    /// The single-argument constructor does **not** verify this; use this
    /// method when strict validation is required.
    public boolean isChecksumValid() {
        return mod97(value) == 1;
    }

    private static int mod97(String s) {
        return Mod97.compute(s);
    }

    @Override
    public String toString() {
        return "Lei(" + value + ")";
    }
}
