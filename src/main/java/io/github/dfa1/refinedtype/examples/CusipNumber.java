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
/// The CUSIP's own check digit is **not** verified — the type guarantees
/// shape, not arithmetic integrity.
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

    public CusipNumber(String value) {
        if (value == null || value.length() != LENGTH) {
            throw new IllegalArgumentException("CUSIP must be exactly 9 characters: " + value);
        }
        String upper = value.toUpperCase();
        if (!PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException("CUSIP must match ^[A-Z0-9]{9}$: " + value);
        }
        this.value = upper;
    }

    @Override
    public String value() {
        return value;
    }

    private static final CountryCode US = new CountryCode("US");

    /// Build the ISO 6166 ISIN: country `US` + CUSIP + Luhn-mod-10 check
    /// digit (see {@link Isin#Isin(CountryCode, String)}).
    public Isin toIsin() {
        return new Isin(US, value);
    }

    @Override
    public String toString() {
        return "CusipNumber(" + value + ")";
    }
}
