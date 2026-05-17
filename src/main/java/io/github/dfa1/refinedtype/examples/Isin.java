package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
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
/// Two constructors are provided:
///
/// - {@link #Isin(String)} parses an already-complete 12-character ISIN
///   and verifies the shape (not the check digit).
/// - {@link #Isin(CountryCode, String)} accepts a {@link CountryCode} and the
///   9-character NSIN, and **computes** the check digit, so the
///   resulting ISIN is guaranteed valid by construction.
///
/// Examples of valid ISINs: `US0378331005` (Apple Inc.),
/// `DE000BASF111` (BASF SE).
public value class Isin implements RefinedString<Isin> {

    private static final int LENGTH = 12;
    private static final int NSIN_LENGTH = 9;
    private static final Pattern PATTERN = Pattern.compile("^[A-Z]{2}[A-Z0-9]{9}[0-9]$");
    private static final Pattern NSIN_PATTERN = Pattern.compile("^[A-Z0-9]{9}$");

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

    /// Build a valid ISIN from a country prefix and a 9-character NSIN,
    /// computing the ISO 6166 check digit (Luhn mod 10 over the
    /// letter-expanded digit string).
    ///
    /// Algorithm:
    ///
    /// 1. Concatenate `country.value()` and the upper-cased NSIN → 11-char base.
    /// 2. Expand each letter to its two-digit value (`A`=10, ..., `Z`=35).
    /// 3. From the right, multiply every other digit by 2; sum the
    ///    digits of every result (`14 → 1 + 4 = 5`).
    /// 4. Check digit = `(10 − sum mod 10) mod 10`.
    public Isin(CountryCode country, String nsin) {
        if (nsin == null || nsin.length() != NSIN_LENGTH) {
            throw new IllegalArgumentException("NSIN must be exactly 9 characters: " + nsin);
        }
        String upperNsin = nsin.toUpperCase();
        if (!NSIN_PATTERN.matcher(upperNsin).matches()) {
            throw new IllegalArgumentException("NSIN must match ^[A-Z0-9]{9}$: " + nsin);
        }
        String base = country.value() + upperNsin;
        this.value = base + computeCheckDigit(base);
    }

    private static int computeCheckDigit(String base) {
        StringBuilder digits = new StringBuilder(base.length() * 2);
        for (int i = 0; i < base.length(); i++) {
            char c = base.charAt(i);
            if (c >= '0' && c <= '9') {
                digits.append(c);
            } else {
                digits.append(c - 'A' + 10);
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
        return (10 - sum % 10) % 10;
    }

    @Override
    public String value() {
        return value;
    }

    /// Two-letter ISO 3166-1 alpha-2 country prefix.
    public CountryCode country() {
        return new CountryCode(value.substring(0, 2));
    }

    @Override
    public String toString() {
        return "Isin(" + value + ")";
    }
}
