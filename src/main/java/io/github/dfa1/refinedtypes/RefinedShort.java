package io.github.dfa1.refinedtypes;

/// Marker for value classes that wrap a single `short` (16-bit signed
/// integer). Provides a uniform `value()` accessor and a default
/// `compareTo` that delegates to {@link Short#compare}.
///
/// The self-type parameter `T` blocks nonsensical cross-type comparisons.
/// Each implementation pins `T` to itself:
///
/// ```java
/// public value class AudioSample implements RefinedShort<AudioSample> { ... }
/// ```
///
/// Without the F-bound, two unrelated `RefinedShort` values would be
/// comparable to each other even though the result is meaningless. With it,
/// only same-type comparisons type-check.
public interface RefinedShort<T extends RefinedShort<T>> extends Comparable<T> {

    short value();

    /// Default ordering by `value()` using {@link Short#compare}.
    /// Override when the natural ordering differs.
    @Override
    default int compareTo(T that) {
        return Short.compare(this.value(), that.value());
    }
}
