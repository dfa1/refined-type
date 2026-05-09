package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedString;

import java.util.regex.Pattern;

/**
 * URL-safe identifier (slug convention) — lowercase alphanumerics with
 * single hyphens between alphanumeric runs.
 *
 * <p>Format: {@code ^[a-z0-9](-?[a-z0-9])*$}, length {@code 1..64}.
 *
 * <p>Refining the type closes a recurring boundary-validation gap when a
 * user-supplied path or resource name flows into routing, persistence,
 * logging, or HTTP headers:
 * <ul>
 *   <li><em>Path traversal</em> — {@code ..} and {@code /} cannot appear.</li>
 *   <li><em>Header injection</em> — CR / LF / NUL cannot appear, so values
 *       embedded in {@code Set-Cookie} or {@code Location} cannot split
 *       headers.</li>
 *   <li><em>ReDoS</em> — the length pre-check makes regex evaluation
 *       linear in the bound, not the input.</li>
 * </ul>
 *
 * <p>Validation is strict — input is <em>not</em> normalised. To convert
 * arbitrary text to a slug, run a {@code slugify} helper upstream and
 * pass the result to the constructor.
 *
 * <p>Slug is a convention, not a spec. Closest standards are
 * <a href="https://www.rfc-editor.org/rfc/rfc3986">RFC 3986</a> (URI
 * unreserved characters) and <a href="https://www.rfc-editor.org/rfc/rfc1123">RFC 1123</a>
 * (DNS labels — see {@link HostName}).
 */
public value class Slug implements RefinedString {

    public static final int MAX_LENGTH = 64;

    private static final Pattern PATTERN = Pattern.compile("^[a-z0-9](-?[a-z0-9])*$");

    private final String value;

    public Slug(String value) {
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

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "Slug(" + value + ")";
    }
}
