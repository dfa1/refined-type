package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedDouble;

/// Geographic latitude in decimal degrees: range [-90.0, 90.0].
///
/// Positive values are north of the equator, negative values south.
/// Stored as `double` to match the convention of every mainstream GIS
/// stack (PostGIS, GeoJSON, Apple `CLLocationDegrees`, Google Maps).
///
/// `double` gives ~16 significant decimal digits — millimeter resolution
/// anywhere on Earth. `float` would give only ~7 digits, sufficient for
/// consumer GPS (~5 m) but not for survey-grade or autonomous-vehicle work.
public value class Latitude implements RefinedDouble<Latitude> {

    public static final double MIN_VALUE = -90.0;
    public static final double MAX_VALUE =  90.0;

    /// Equator.
    public static final Latitude ZERO       = new Latitude(0.0);
    public static final Latitude NORTH_POLE = new Latitude(MAX_VALUE);
    public static final Latitude SOUTH_POLE = new Latitude(MIN_VALUE);

    private final double value;

    public Latitude(double value) {
        if (Double.isNaN(value) || value < MIN_VALUE || value > MAX_VALUE) {
            throw new IllegalArgumentException("latitude must be in [-90, 90]: " + value);
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

    /// True iff strictly north of the equator (`value > 0`).
    public boolean isNorthernHemisphere() {
        return value > 0.0;
    }

    /// True iff strictly south of the equator (`value < 0`).
    public boolean isSouthernHemisphere() {
        return value < 0.0;
    }

    /// True iff exactly on the equator.
    public boolean isEquator() {
        return value == 0.0;
    }

    @Override
    public String toString() {
        return "Latitude(" + value + ")";
    }
}
