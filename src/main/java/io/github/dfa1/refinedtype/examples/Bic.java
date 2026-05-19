package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;

import java.util.regex.Pattern;

/// Bank Identifier Code (ISO 9362) — the globally unique identifier for
/// banks and financial institutions used in international wire transfers.
///
/// Layout: `BBBB CC LL [BBB]`
///
/// - `BBBB` — 4-letter institution code (letters only).
/// - `CC`   — 2-letter country code (ISO 3166-1 alpha-2), see {@link #country()}.
/// - `LL`   — 2-character location code (letters or digits).
/// - `BBB`  — 3-character branch code (letters or digits); `XXX` = primary office.
///
/// The constructor accepts both 8-character (branch omitted) and 11-character
/// forms. **Storage is always the 11-character canonical form** — 8-character
/// input is expanded by appending `XXX`. Call {@link #toBic8()} to recover the
/// short form when needed.
///
/// Examples: `DEUTDEDB` (Deutsche Bank Frankfurt, primary office)
/// → stored as `DEUTDEDBXXX`; `DEUTDEDBFRA` (Frankfurt branch) stored as-is.
public value class Bic implements RefinedString<Bic> {

    private static final Pattern BIC8  = Pattern.compile("^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}$");
    private static final Pattern BIC11 = Pattern.compile("^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}[A-Z0-9]{3}$");

    private final String value;

    /// Accepts an 8- or 11-character BIC string (case-insensitive).
    /// 8-character input is expanded to canonical 11-character form by appending `XXX`.
    public Bic(String value) {
        if (value == null) {
            throw new IllegalArgumentException("BIC must not be null");
        }
        String upper = value.toUpperCase();
        if (upper.length() == 8 && BIC8.matcher(upper).matches()) {
            this.value = upper + "XXX";
        } else if (upper.length() == 11 && BIC11.matcher(upper).matches()) {
            this.value = upper;
        } else {
            throw new IllegalArgumentException(
                    "BIC must be 8 characters (BBBBCCLL) or 11 characters (BBBBCCLLBBB)" +
                    " — letters A-Z for bank/country codes, alphanumeric elsewhere: " + value);
        }
    }

    @Override
    public String value() {
        return value;
    }

    /// 4-letter institution code (positions 1–4).
    public String bankCode() {
        return value.substring(0, 4);
    }

    /// ISO 3166-1 alpha-2 country code embedded in the BIC (positions 5–6).
    public CountryCode country() {
        return new CountryCode(value.substring(4, 6));
    }

    /// 2-character location code (positions 7–8, letters or digits).
    public String locationCode() {
        return value.substring(6, 8);
    }

    /// 3-character branch code (positions 9–11, letters or digits).
    /// Always present since the value is stored in canonical 11-character form.
    /// `XXX` denotes the primary office.
    public String branchCode() {
        return value.substring(8, 11);
    }

    /// Returns `true` when this BIC identifies the primary office (`XXX` branch code).
    public boolean isPrimaryOffice() {
        return "XXX".equals(branchCode());
    }

    /// Returns the 8-character short form.
    /// For primary-office BICs the `XXX` suffix is stripped; for branch BICs the
    /// full 11-character string is returned unchanged.
    public String toBic8() {
        return isPrimaryOffice() ? value.substring(0, 8) : value;
    }

    @Override
    public String toString() {
        return "Bic(" + value + ")";
    }
}
