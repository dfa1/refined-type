package io.github.dfa1.refinedtypes;

/// Marker for value classes that wrap a single `float` (32-bit IEEE 754).
/// Provides a uniform `value()` accessor and a default `compareTo` that
/// delegates to {@link Float#compare}.
///
/// The self-type parameter `T` blocks nonsensical cross-type comparisons.
/// Each implementation pins `T` to itself:
///
/// ```java
/// public value class Probability implements RefinedFloat<Probability> { ... }
/// public value class Price       implements RefinedFloat<Price>       { ... }
/// ```
///
/// Without the F-bound, `probability.compareTo(price)` would compile despite
/// being semantically meaningless. With it, only same-type comparisons
/// type-check.
public interface RefinedFloat<T extends RefinedFloat<T>> extends Comparable<T> {

    float value();

    /// Default ordering by `value()` using {@link Float#compare}.
    /// Override when the natural ordering differs.
    @Override
    default int compareTo(T that) {
        return Float.compare(this.value(), that.value());
    }
}
