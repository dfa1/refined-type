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

    /// Build the ISO 6166 ISIN: `"CH"` + 9-digit zero-padded valor +
    /// Luhn-mod-10 check digit.
    ///
    /// Algorithm (per ISO 6166):
    ///
    /// 1. Concatenate the country prefix `CH` with the valor padded to
    ///    nine digits → eleven-character base.
    /// 2. Expand each letter to its two-digit value (`A`=10, `B`=11,
    ///    ..., `Z`=35); digits pass through.
    /// 3. From the right, multiply every other digit by 2. Sum the
    ///    digits of every result (so `14 → 1 + 4 = 5`).
    /// 4. Check digit = `(10 − sum mod 10) mod 10`.
    public Isin toIsin() {
        String base = "CH" + String.format("%09d", value);
        StringBuilder digits = new StringBuilder(base.length() * 2);
        for (int i = 0; i < base.length(); i++) {
            char c = base.charAt(i);
            if (Character.isDigit(c)) {
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
                if (d >= 10) d -= 9; // equivalent to summing the two digits
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        int check = (10 - sum % 10) % 10;
        return new Isin(base + check);
    }

    @Override
    public String toString() {
        return "SwissValorNumber(" + value + ")";
    }
}
