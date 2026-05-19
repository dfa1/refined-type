package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;

/// ISO 3166-1 alpha-2 country code — exactly two ASCII letters, stored
/// uppercase.
///
/// Refining lifts country codes out of `String`, so an API can declare
/// `CountryCode origin` instead of `String origin` and the compiler will
/// reject mixed-up locale tags, currency codes, or free-form text.
///
/// Input is uppercased on construction (`"jp"` and `"JP"` are equivalent
/// inputs producing the same value). No registry lookup is performed —
/// the type guarantees *shape*, not *membership*. A consumer that needs
/// "is this a currently-assigned code" should validate against an
/// up-to-date ISO 3166-1 list.
public value class CountryCode implements RefinedString<CountryCode> {

    private final String value;

    private CountryCode(String value) {
        if (value == null || value.length() != 2) {
            throw new IllegalArgumentException("ISO 3166-1 alpha-2 code must be exactly 2 characters: " + value);
        }
        String upper = value.toUpperCase();
        if (!Character.isLetter(upper.charAt(0)) || !Character.isLetter(upper.charAt(1))) {
            throw new IllegalArgumentException("ISO 3166-1 alpha-2 code must be two letters: " + value);
        }
        this.value = upper;
    }

    public static CountryCode of(String value) {
        return new CountryCode(value);
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "CountryCode(" + value + ")";
    }
}
