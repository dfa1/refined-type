package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;

/// ISO 4217 currency code — exactly three ASCII letters, stored uppercase.
///
/// Refining lifts currency codes out of `String`, so an API can declare
/// `CurrencyCode currency` instead of `String currency` and the compiler
/// will reject swapped arguments, country codes, or free-form text.
///
/// Input is uppercased on construction (`"eur"` and `"EUR"` are equivalent
/// inputs producing the same value). No registry lookup is performed —
/// the type guarantees *shape*, not *membership*. A consumer that needs
/// "is this a currently-active code" should validate against an up-to-date
/// ISO 4217 list.
public value class CurrencyCode implements RefinedString<CurrencyCode> {

    private final String value;

    public CurrencyCode(String value) {
        if (value == null || value.length() != 3) {
            throw new IllegalArgumentException("ISO 4217 currency code must be exactly 3 letters: " + value);
        }
        String upper = value.toUpperCase();
        if (!Character.isLetter(upper.charAt(0))
                || !Character.isLetter(upper.charAt(1))
                || !Character.isLetter(upper.charAt(2))) {
            throw new IllegalArgumentException("ISO 4217 currency code must be three letters: " + value);
        }
        this.value = upper;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "CurrencyCode(" + value + ")";
    }
}
