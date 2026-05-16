package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedString;

/// Email address — coarse syntactic check, not RFC 5322 conformant.
///
/// Validation enforced at construction:
///
/// - Non-blank
/// - Exactly one `@`
/// - Non-empty local part (text before the `@`)
/// - Domain part contains at least one `.`
///
/// The aim is to catch obvious typos and stop empty strings, not to
/// implement RFC 5322 (which permits forms like `"quoted local"@host`
/// that almost no production system actually accepts). Treat this as
/// the kind of validation a `<input type="email">` browser control
/// performs — enough to gate API entry, not a replacement for sending
/// a confirmation message.
///
/// The address is stored *as-given*: case is preserved on both sides
/// of the `@`. Consumers that need case-insensitive equality should
/// normalize themselves; the type does not impose a policy.
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
