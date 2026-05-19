package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedInt;

/// SIX Swiss Exchange security identifier — the *Valoren-Nummer*.
///
/// Issued by the SIX group's numbering agency and printed on every
/// Swiss tradable instrument (stocks, bonds, funds, derivatives). The
/// modern format is 1 to 9 decimal digits; older paper certificates
/// sometimes carried fewer.
///
/// The valor is the **national** identifier. To produce the
/// international ISO 6166 ISIN, prepend the country prefix `CH`,
/// left-pad the valor to nine digits, and append the Luhn-style ISIN
/// check digit — see {@link #toIsin()}.
///
/// Example: ABB Ltd has valor `1_222_171`; its ISIN is `CH0012221716`.
public value class SwissValorNumber implements RefinedInt<SwissValorNumber> {

    private static final int MIN_RAW = 1;
    private static final int MAX_RAW = 999_999_999;

    public static final SwissValorNumber MIN = new SwissValorNumber(MIN_RAW);
    public static final SwissValorNumber MAX = new SwissValorNumber(MAX_RAW);

    private final int value;

    private SwissValorNumber(int value) {
        if (value < MIN_RAW || value > MAX_RAW) {
            throw new IllegalArgumentException("Swiss valor must be in [1, 999_999_999]: " + value);
        }
        this.value = value;
    }

    public static SwissValorNumber of(int value) {
        return new SwissValorNumber(value);
    }

    @Override
    public int value() {
        return value;
    }

    private static final CountryCode CH = CountryCode.of("CH");

    /// Build the ISO 6166 ISIN: country `CH` + 9-digit zero-padded valor +
    /// Luhn-mod-10 check digit (see {@link Isin#of(CountryCode, String)}).
    public Isin toIsin() {
        return Isin.of(CH, String.format("%09d", value));
    }

    @Override
    public String toString() {
        return "SwissValorNumber(" + value + ")";
    }
}
