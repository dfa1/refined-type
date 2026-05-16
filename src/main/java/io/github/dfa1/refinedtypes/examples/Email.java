package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedString;

public value class Email implements RefinedString<Email> {

    private final String value;

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        int at = value.indexOf('@');
        if (at <= 0) {
            throw new IllegalArgumentException("email missing local part before '@': " + value);
        }
        if (at != value.lastIndexOf('@')) {
            throw new IllegalArgumentException("email must contain exactly one '@': " + value);
        }
        String domain = value.substring(at + 1);
        if (domain.isEmpty() || !domain.contains(".")) {
            throw new IllegalArgumentException("email domain must contain a '.': " + value);
        }
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "Email(" + value + ")";
    }
}
