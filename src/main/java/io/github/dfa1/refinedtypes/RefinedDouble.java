package io.github.dfa1.refinedtypes;

/// Marker for value classes that wrap a single `double` (64-bit IEEE 754).
/// Provides a uniform `value()` accessor and a default `compareTo` that
/// delegates to {@link Double#compare}.
///
/// The self-type parameter `T` blocks nonsensical cross-type comparisons.
/// Each implementation pins `T` to itself:
///
/// ```java
/// public value class Latitude  implements RefinedDouble<Latitude>  { ... }
/// public value class Longitude implements RefinedDouble<Longitude> { ... }
/// ```
///
/// Without the F-bound, `latitude.compareTo(longitude)` would compile despite
/// being semantically meaningless. With it, only same-type comparisons
/// type-check.
public interface RefinedDouble<T extends RefinedDouble<T>> extends Comparable<T> {

    double value();

    /// Default ordering by `value()` using {@link Double#compare}.
    /// Override when the natural ordering differs.
    @Override
    default int compareTo(T that) {
        return Double.compare(this.value(), that.value());
    }
}
