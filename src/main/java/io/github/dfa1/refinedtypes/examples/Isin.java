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
