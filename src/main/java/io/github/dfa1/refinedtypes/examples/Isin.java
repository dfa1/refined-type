package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedString;
import java.util.regex.Pattern;

/// International Securities Identification Number (ISO 6166) — the
/// 12-character code used to identify a tradable security across markets.
///
/// Layout: `CC AAAAAAAAA D` where
///
/// - `CC` is a two-letter ISO 3166-1 alpha-2 country prefix (e.g. `US`,
///   `DE`), exposed via {@link #country()}.
/// - `AAAAAAAAA` is a nine-character National Securities Identifying
///   Number — letters or digits, assigned by the local numbering agency.
/// - `D` is a single check digit.
///
/// Input is uppercased on construction. The pattern is validated but the
/// Luhn-style check digit is **not** verified — this type guarantees
/// shape, not arithmetic integrity. Callers that need that guarantee
/// should run the standard ISIN check-digit algorithm in addition.
///
/// Examples of valid ISINs: `US0378331005` (Apple Inc.),
/// `DE000BASF111` (BASF SE).
public value class Isin implements RefinedString<Isin> {

    private static final int LENGTH = 12;
    private static final Pattern PATTERN = Pattern.compile("^[A-Z]{2}[A-Z0-9]{9}[0-9]$");

    private final String value;

    public Isin(String value) {
        if (value == null || value.length() != LENGTH) {
            throw new IllegalArgumentException("ISIN must be exactly 12 characters: " + value);
        }
        String upper = value.toUpperCase();
        if (!PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException("ISIN must match ^[A-Z]{2}[A-Z0-9]{9}[0-9]$: " + value);
        }
        this.value = upper;
    }

    /// Compute the ISO 6166 check digit for an 11-character base
    /// (country prefix + 9-char NSIN) and return the resulting full ISIN.
    ///
    /// Algorithm (Luhn mod 10 over the letter-expanded digit string):
    ///
    /// 1. Expand each letter to its two-digit value (`A`=10, ..., `Z`=35).
    /// 2. From the right, multiply every other digit by 2; sum the
    ///    digits of every result (`14 → 1 + 4 = 5`).
    /// 3. Check digit = `(10 − sum mod 10) mod 10`.
    public static Isin fromBase(String base) {
        if (base == null || base.length() != 11) {
            throw new IllegalArgumentException("ISIN base must be exactly 11 characters: " + base);
        }
        String upper = base.toUpperCase();
        StringBuilder digits = new StringBuilder(upper.length() * 2);
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            if (c >= '0' && c <= '9') {
                digits.append(c);
            } else if (c >= 'A' && c <= 'Z') {
                digits.append(c - 'A' + 10);
            } else {
                throw new IllegalArgumentException("invalid ISIN base char '" + c + "': " + base);
            }
        }
        int sum = 0;
        boolean doubleIt = true; // rightmost digit doubles first
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doubleIt) {
                d *= 2;
                if (d >= 10) d -= 9;
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        int check = (10 - sum % 10) % 10;
        return new Isin(upper + check);
    }

    @Override
    public String value() {
        return value;
    }

    /// Two-letter ISO 3166-1 alpha-2 country prefix.
    public Country country() {
        return new Country(value.substring(0, 2));
    }

    @Override
    public String toString() {
        return "Isin(" + value + ")";
    }
}
