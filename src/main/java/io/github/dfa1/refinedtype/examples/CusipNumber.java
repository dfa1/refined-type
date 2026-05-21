package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;

import java.util.regex.Pattern;

/// CUSIP — *Committee on Uniform Securities Identification Procedures*.
/// The nine-character alphanumeric identifier issued by CUSIP Global
/// Services and used to identify North American securities (US and
/// Canadian stocks, bonds, funds).
///
/// Layout: `IIIIII II C` where
///
/// - `IIIIII` is the six-character issuer code.
/// - `II` is the two-character issue code (specific security from the issuer).
/// - `C` is a single check digit.
///
/// The character set is `[A-Z0-9]`. Input is uppercased on construction.
/// The check digit (position 9) is verified on construction using the
/// CUSIP Luhn mod-10 algorithm: map each of the first 8 characters to its
/// value (digit → 0–9, letter → A=10...Z=35), double values at even positions
/// (2nd, 4th, 6th, 8th; 1-indexed), accumulate digit sums,
/// check digit = (10 − total mod 10) mod 10.
///
/// The CUSIP is the **national** identifier. To produce the international
/// ISO 6166 ISIN, prepend the country prefix `US` and append the ISIN
/// check digit — see {@link #toIsin()}.
///
/// Example: Apple Inc. has CUSIP `037833100`; its ISIN is `US0378331005`.
public value class CusipNumber implements RefinedString<CusipNumber> {

    private static final int LENGTH = 9;
    private static final Pattern PATTERN = Pattern.compile("^[A-Z0-9]{9}$");

    private final String value;

    private CusipNumber(String value) {
        if (value == null || value.length() != LENGTH) {
            throw new IllegalArgumentException("CUSIP must be exactly 9 characters: " + value);
        }
        String upper = value.toUpperCase();
        if (!PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException("CUSIP must match ^[A-Z0-9]{9}$: " + value);
        }
        int expected = computeCheckDigit(upper);
        int actual = upper.charAt(8) - '0';
        if (actual != expected) {
            throw new IllegalArgumentException(
                    "CUSIP check digit invalid (expected " + expected + ", got " + actual + "): " + value);
        }
        this.value = upper;
    }

    private static int computeCheckDigit(String cusip) {
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            char c = cusip.charAt(i);
            int v = (c >= '0' && c <= '9') ? c - '0' : c - 'A' + 10;
            if ((i & 1) == 1) {  // even position (1-indexed) = odd index (0-indexed)
                v *= 2;
            }
            sum += v / 10 + v % 10;
        }
        return (10 - sum % 10) % 10;
    }

    public static CusipNumber of(String value) {
        return new CusipNumber(value);
    }

    @Override
    public String value() {
        return value;
    }

    private static final CountryCode US = CountryCode.of("US");

    /// Build the ISO 6166 ISIN: country `US` + CUSIP + Luhn-mod-10 check
    /// digit (see {@link Isin#of(CountryCode, String)}).
    public Isin toIsin() {
        return Isin.of(US, value);
    }

    @Override
    public String toString() {
        return "CusipNumber(" + value + ")";
    }
}
