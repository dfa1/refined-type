package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedString;

public value class Country implements RefinedString<Country> {

    private final String value;

    public Country(String value) {
        if (value == null || value.length() != 2) {
            throw new IllegalArgumentException("ISO 3166-1 alpha-2 code must be exactly 2 characters: " + value);
        }
        String upper = value.toUpperCase();
        if (!Character.isLetter(upper.charAt(0)) || !Character.isLetter(upper.charAt(1))) {
            throw new IllegalArgumentException("ISO 3166-1 alpha-2 code must be two letters: " + value);
        }
        this.value = upper;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "Country(" + value + ")";
    }
}
