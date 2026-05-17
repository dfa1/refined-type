package io.github.dfa1.refinedtype;

/// Marker for value classes that wrap a single `int` constrained to some
/// subset of the int domain (e.g. [0, 150] for {@code Age}, [1, 65535]
/// for {@code Port}). Provides a uniform `value()` accessor and a default
/// `compareTo` that delegates to {@link Integer#compare}.
///
/// The self-type parameter `T` blocks nonsensical cross-type comparisons.
/// Each implementation pins `T` to itself:
///
/// ```java
/// public value class Age  implements RefinedInt<Age>  { ... }
/// public value class Port implements RefinedInt<Port> { ... }
/// ```
///
/// Without the F-bound, `age.compareTo(port)` would compile even though
/// the comparison is meaningless. With it, only same-type comparisons
/// type-check.
public interface RefinedInt<T extends RefinedInt<T>> extends Comparable<T> {

    int value();

    /// Default ordering by `value()` using {@link Integer#compare}.
    /// Override when the natural ordering differs — e.g. unsigned types
    /// (see {@code UnsignedInt}) must use {@link Integer#compareUnsigned}.
    @Override
    default int compareTo(T that) {
        return Integer.compare(this.value(), that.value());
    }
}
