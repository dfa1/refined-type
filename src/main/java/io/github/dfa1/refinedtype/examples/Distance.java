package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedDouble;

/// Distance — non-negative, finite, stored in metres.
///
/// Returned by every great-circle / Euclidean operation in this package
/// instead of a bare `double`, so call sites cannot confuse metres with
/// kilometres, miles, or "some number, probably distance".
///
/// SI base unit is the metre. Construct with {@link #of(double, Unit)}
/// and convert back via {@link #to(Unit)}; the storage is always metres.
public value class Distance implements RefinedDouble<Distance> {

    /// Conversion factors to the base unit (metres).
    /// `meters` is the number of metres in one of the unit.
    public enum Unit {
        M             (1.0),
        KM            (1_000.0),
        MILE          (1_609.344),       // international mile
        NAUTICAL_MILE (1_852.0);

        public final double meters;

        Unit(double meters) {
            this.meters = meters;
        }
    }

    public static final Distance ZERO = new Distance(0.0);

    private final double meters;

    private Distance(double meters) {
        if (Double.isNaN(meters) || Double.isInfinite(meters) || meters < 0.0) {
            throw new IllegalArgumentException("distance must be finite and non-negative: " + meters);
        }
        this.meters = meters;
    }

    /// Construct from a value in metres.
    public static Distance of(double meters) {
        return new Distance(meters);
    }

    /// Construct from a quantity in the given unit.
    public static Distance of(double n, Unit unit) {
        return new Distance(n * unit.meters);
    }

    @Override
    public double value() {
        return meters;
    }

    /// Convert to a real-valued quantity of the given unit.
    public double to(Unit unit) {
        return meters / unit.meters;
    }

    @Override
    public String toString() {
        return "Distance(" + meters + " m)";
    }
}
