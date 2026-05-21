package io.github.dfa1.refinedtype.bench;

/// Identity-class mirror of {@link io.github.dfa1.refinedtype.examples.Email}.
/// Used only in benchmarks to isolate the value-class flattening benefit
/// from the per-wrapper heap object cost of a regular identity class.
class EmailIdentity {

    private final String value;

    EmailIdentity(String value) {
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

    String value() {
        return value;
    }
}
