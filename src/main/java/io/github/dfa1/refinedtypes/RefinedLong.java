package io.github.dfa1.refinedtypes;

/// Marker for value classes that wrap a single `long` (64-bit signed
/// integer). Provides a uniform `value()` accessor and a default
/// `compareTo` that delegates to {@link Long#compare}.
///
/// The self-type parameter `T` blocks nonsensical cross-type comparisons.
/// Each implementation pins `T` to itself:
///
/// ```java
/// public value class Size implements RefinedLong<Size> { ... }
/// ```
///
/// Without the F-bound, two unrelated `RefinedLong` values would be
/// comparable to each other even though the result is meaningless. With it,
/// only same-type comparisons type-check.
public interface RefinedLong<T extends RefinedLong<T>> extends Comparable<T> {

    long value();

    /// Default ordering by `value()` using {@link Long#compare}.
    /// Override when the natural ordering differs — e.g. unsigned types
    /// must use {@link Long#compareUnsigned}.
    @Override
    default int compareTo(T that) {
        return Long.compare(this.value(), that.value());
    }
}
