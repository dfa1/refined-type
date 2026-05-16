package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedDouble;

/// Geographic longitude in decimal degrees: range [-180.0, 180.0].
///
/// Positive values are east of the prime meridian, negative values west.
/// `+180.0` and `-180.0` denote the same meridian — both are accepted;
/// no canonicalisation is performed.
///
/// Stored as `double` for the same reason as {@link Latitude}: GIS
/// convention and the ~16 digits of precision required for sub-meter
/// resolution near the equator.
public value class Longitude implements RefinedDouble<Longitude> {

    public static final double MIN_VALUE = -180.0;
    public static final double MAX_VALUE =  180.0;

    /// Prime meridian.
    public static final Longitude ZERO = new Longitude(0.0);

    private final double value;

    public Longitude(double value) {
        if (Double.isNaN(value) || value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("longitude must be in [-180, 180]: " + value);
        }
        this.value = value;
    }

    @Override
    public double value() {
        return value;
    }

    /// Value converted to radians — convenient for trigonometric formulas.
    public double toRadians() {
        return Math.toRadians(value);
    }

    /// True iff strictly east of the prime meridian (`value > 0`).
    public boolean isEasternHemisphere() {
        return value > 0.0;
    }

    /// True iff strictly west of the prime meridian (`value < 0`).
    public boolean isWesternHemisphere() {
        return value < 0.0;
    }

    /// True iff exactly on the prime meridian.
    public boolean isPrimeMeridian() {
        return value == 0.0;
    }

    @Override
    public String toString() {
        return "Longitude(" + value + ")";
    }
}
