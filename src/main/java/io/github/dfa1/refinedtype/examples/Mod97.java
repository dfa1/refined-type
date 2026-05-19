package io.github.dfa1.refinedtype.examples;

/// ISO 7064 MOD 97-10 algorithm shared by {@link Lei} and {@link Iban}.
///
/// Processes in chunks of digits to avoid `BigInteger` overhead; an
/// `int` accumulator is wide enough for 9-character blocks.
final class Mod97 {

    private Mod97() {}

    /// Returns `s mod 97` after substituting each letter `A`–`Z` with
    /// its numeric value (`A`=10, …, `Z`=35).
    static int compute(String s) {
        StringBuilder numeric = new StringBuilder(s.length() * 2);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                numeric.append(c);
            } else {
                numeric.append(c - 'A' + 10);
            }
        }
        int remainder = 0;
        for (int i = 0; i < numeric.length(); i++) {
            remainder = (remainder * 10 + (numeric.charAt(i) - '0')) % 97;
        }
        return remainder;
    }
}
