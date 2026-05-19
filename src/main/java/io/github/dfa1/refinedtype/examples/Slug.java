package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;

import java.util.regex.Pattern;

/// URL-safe identifier (slug convention) — lowercase alphanumerics with
/// single hyphens between alphanumeric runs.
///
/// Format: `^[a-z0-9](-?[a-z0-9])*$`, length `1..64`.
///
/// Refining the type closes a recurring boundary-validation gap when a
/// user-supplied path or resource name flows into routing, persistence,
/// logging, or HTTP headers:
///
/// - *Path traversal* — `..` and `/` cannot appear.
/// - *Header injection* — CR / LF / NUL cannot appear, so values
///   embedded in `Set-Cookie` or `Location` cannot split
///   headers.
/// - *ReDoS* — the length pre-check makes regex evaluation
///   linear in the bound, not the input.
///
/// Validation is strict — input is *not* normalized. To convert
/// arbitrary text to a slug, run a `slugify` helper upstream and
/// pass the result to the constructor.
///
/// Slug is a convention, not a spec. Closest standards are
/// [RFC 3986](https://www.rfc-editor.org/rfc/rfc3986) (URI
/// unreserved characters) and [RFC 1123](https://www.rfc-editor.org/rfc/rfc1123)
/// (DNS labels — see {@link HostName}).
public value class Slug implements RefinedString<Slug> {

    public static final int MAX_LENGTH = 64;

    private static final Pattern PATTERN = Pattern.compile("^[a-z0-9](-?[a-z0-9])*$");

    private final String value;

    private Slug(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("slug must not be empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("slug exceeds 64 characters: " + value.length());
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "slug must match ^[a-z0-9](-?[a-z0-9])*$ (lowercase, single hyphens): " + value);
        }
        this.value = value;
    }

    public static Slug of(String value) {
        return new Slug(value);
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "Slug(" + value + ")";
    }
}
