package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedInt;

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

    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 999_999_999;

    private final int value;

    public SwissValorNumber(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("Swiss valor must be in [1, 999_999_999]: " + value);
        }
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }

    private static final CountryCode CH = new CountryCode("CH");

    /// Build the ISO 6166 ISIN: country `CH` + 9-digit zero-padded valor +
    /// Luhn-mod-10 check digit (see {@link Isin#Isin(CountryCode, String)}).
    public Isin toIsin() {
        return new Isin(CH, String.format("%09d", value));
    }

    @Override
    public String toString() {
        return "SwissValorNumber(" + value + ")";
    }
}
