package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;

import java.util.regex.Pattern;

/// International Bank Account Number (ISO 13616) — the standard for
/// identifying bank accounts across national borders.
///
/// Layout: `CC KK BBAN`
///
/// - `CC`   — 2-letter ISO 3166-1 alpha-2 country code, see {@link #country()}.
/// - `KK`   — 2-digit MOD 97-10 check digits, see {@link #checkDigits()}.
/// - `BBAN` — Basic Bank Account Number; country-specific alphanumeric string,
///             see {@link #bban()}.
///
/// The constructor validates both structure and MOD 97-10 checksum. Spaces
/// are stripped before validation so human-readable grouped forms
/// (e.g. `DE89 3704 0044 0532 0130 00`) are accepted.
///
/// Use {@link #of(CountryCode, String)} to build a valid IBAN from a country
/// code and a raw BBAN — check digits are computed automatically.
///
/// Examples: `DE89370400440532013000` (Deutsche Bank),
/// `GB82WEST12345698765432` (NatWest).
public value class Iban implements RefinedString<Iban> {

    private static final int MIN_LENGTH = 15;
    private static final int MAX_LENGTH = 34;

    private static final Pattern PATTERN      = Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$");
    private static final Pattern BBAN_PATTERN = Pattern.compile("^[A-Z0-9]{1,30}$");

    private final String value;

    /// Parse and validate an IBAN string. Spaces are stripped; input is uppercased.
    ///
    /// Throws {@link IllegalArgumentException} if the string does not match the
    /// expected structure or if the MOD 97-10 checksum is incorrect.
    public Iban(String value) {
        if (value == null) {
            throw new IllegalArgumentException("IBAN must not be null");
        }
        String upper = value.replaceAll("\\s+", "").toUpperCase();
        if (upper.length() < MIN_LENGTH || upper.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "IBAN must be " + MIN_LENGTH + "–" + MAX_LENGTH + " characters (excluding spaces): " + value);
        }
        if (!PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException(
                    "IBAN must match CC KK BBAN: 2 uppercase letters + 2 digits + alphanumeric BBAN: " + value);
        }
        if (Mod97.compute(upper.substring(4) + upper.substring(0, 4)) != 1) {
            throw new IllegalArgumentException("IBAN checksum invalid (MOD 97-10): " + value);
        }
        this.value = upper;
    }

    /// Build a valid IBAN from a country code and a BBAN by computing the
    /// MOD 97-10 check digits.
    ///
    /// Algorithm:
    ///
    /// 1. Rearrange: append `CC` + `"00"` to the BBAN.
    /// 2. Replace each letter with its numeric value (`A`=10, …, `Z`=35).
    /// 3. Compute the result modulo 97.
    /// 4. Check digits = 98 − result (always in [2, 98], zero-padded to 2 digits).
    public static Iban of(CountryCode country, String bban) {
        if (bban == null) {
            throw new IllegalArgumentException("BBAN must not be null");
        }
        String upperBban = bban.replaceAll("\\s+", "").toUpperCase();
        if (!BBAN_PATTERN.matcher(upperBban).matches()) {
            throw new IllegalArgumentException(
                    "BBAN must be 1–30 alphanumeric characters (A-Z, 0-9): " + bban);
        }
        int r = Mod97.compute(upperBban + country.value() + "00");
        int check = 98 - r;
        String full = country.value() + String.format("%02d", check) + upperBban;
        return new Iban(full);
    }

    @Override
    public String value() {
        return value;
    }

    /// ISO 3166-1 alpha-2 country code (positions 1–2).
    public CountryCode country() {
        return CountryCode.of(value.substring(0, 2));
    }

    /// Two-digit MOD 97-10 check digits as a string (positions 3–4), e.g. `"89"` or `"02"`.
    ///
    /// Returns `String` rather than `int` to preserve zero-padding (e.g. `"02"`, not `2`);
    /// the IBAN spec treats this as a 2-character string in the rearrangement step.
    public String checkDigits() {
        return value.substring(2, 4);
    }

    /// Basic Bank Account Number — the country-specific part (positions 5+).
    ///
    /// Returns `String` because the BBAN is alphanumeric and country-specific;
    /// no universal numeric or structural invariant exists beyond what the parent `Iban` already enforces.
    public String bban() {
        return value.substring(4);
    }

    @Override
    public String toString() {
        return "Iban(" + value + ")";
    }
}
