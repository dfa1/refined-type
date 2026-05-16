package io.github.dfa1.refinedtypes;

/// Marker for value classes that wrap a single `String` constrained by
/// some validation rule (e.g. a syntactic pattern or character whitelist).
/// Provides a uniform `value()` accessor and a default `compareTo` that
/// delegates to {@link String#compareTo}.
///
/// The self-type parameter `T` blocks nonsensical cross-type comparisons.
/// Each implementation pins `T` to itself:
///
/// ```java
/// public value class Email   implements RefinedString<Email>   { ... }
/// public value class Country implements RefinedString<Country> { ... }
/// ```
///
/// Without the F-bound, `email.compareTo(country)` would compile despite
/// being semantically meaningless. With it, only same-type comparisons
/// type-check.
public interface RefinedString<T extends RefinedString<T>> extends Comparable<T> {

    String value();

    /// Default ordering by `value()` using {@link String#compareTo} (lexicographic).
    /// Override when the natural ordering differs — e.g. case-insensitive
    /// or locale-aware ordering.
    @Override
    default int compareTo(T that) {
        return this.value().compareTo(that.value());
    }
}
