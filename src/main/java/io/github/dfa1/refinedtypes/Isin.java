package io.github.dfa1.refinedtypes;

import java.util.regex.Pattern;

public value class Isin implements RefinedString {

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

    /** Two-letter ISO 3166-1 alpha-2 country prefix. */
    public Country country() {
        return new Country(value.substring(0, 2));
    }

    @Override
    public String toString() {
        return "Isin(" + value + ")";
    }
}
