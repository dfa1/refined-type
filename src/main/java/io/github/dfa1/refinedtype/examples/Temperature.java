package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedDouble;

/// Temperature — finite, stored in Kelvin (SI base unit).
///
/// The only hard constraint is `value >= 0 K` (absolute zero is the physical
/// lower bound; there is no upper bound).  Construct with
/// {@link #of(double, Unit)} to supply a value in any unit; convert back via
/// {@link #to(Unit)}.  Storage is always Kelvin.
///
/// Key constants:
///
/// - `ABSOLUTE_ZERO` — 0 K, coldest physically possible state
/// - `WATER_FREEZING` — 273.15 K (0 °C / 32 °F) at standard pressure
/// - `WATER_BOILING`  — 373.15 K (100 °C / 212 °F) at standard pressure
/// - `STANDARD`       — 293.15 K (20 °C / 68 °F), ISO standard room temperature
/// - `BODY`           — 310.15 K (37 °C / 98.6 °F), normal human body temperature
public value class Temperature implements RefinedDouble<Temperature> {

    public enum Unit {
        KELVIN {
            @Override public double toKelvin(double v)   { return v; }
            @Override public double fromKelvin(double k) { return k; }
        },
        CELSIUS {
            @Override public double toKelvin(double v)   { return v + 273.15; }
            @Override public double fromKelvin(double k) { return k - 273.15; }
        },
        FAHRENHEIT {
            @Override public double toKelvin(double v)   { return (v - 32.0) * 5.0 / 9.0 + 273.15; }
            @Override public double fromKelvin(double k) { return (k - 273.15) * 9.0 / 5.0 + 32.0; }
        };

        public abstract double toKelvin(double v);
        public abstract double fromKelvin(double k);
    }

    public static final Temperature ABSOLUTE_ZERO  = new Temperature(0.0);
    public static final Temperature WATER_FREEZING = new Temperature(273.15);
    public static final Temperature WATER_BOILING  = new Temperature(373.15);
    public static final Temperature STANDARD       = new Temperature(293.15);
    public static final Temperature BODY           = new Temperature(310.15);

    private final double kelvin;

    private Temperature(double kelvin) {
        if (Double.isNaN(kelvin) || Double.isInfinite(kelvin)) {
            throw new IllegalArgumentException("temperature must be finite: " + kelvin);
        }
        if (kelvin < 0.0) {
            throw new IllegalArgumentException("temperature cannot be below absolute zero: " + kelvin + " K");
        }
        this.kelvin = kelvin;
    }

    /// Construct from a value in Kelvin.
    public static Temperature of(double kelvin) {
        return new Temperature(kelvin);
    }

    /// Construct from a value in the given unit.
    public static Temperature of(double value, Unit unit) {
        return new Temperature(unit.toKelvin(value));
    }

    @Override
    public double value() {
        return kelvin;
    }

    /// Convert to the given unit.
    public double to(Unit unit) {
        return unit.fromKelvin(kelvin);
    }

    /// Returns `true` if this temperature is at absolute zero (0 K).
    public boolean isAbsoluteZero() {
        return kelvin == 0.0;
    }

    /// Returns `true` if water freezes at this temperature or below (≤ 273.15 K).
    public boolean isFreezing() {
        return kelvin <= 273.15;
    }

    /// Returns `true` if water boils at this temperature or above (≥ 373.15 K).
    public boolean isBoiling() {
        return kelvin >= 373.15;
    }

    /// Returns `true` if this temperature falls in the cryogenic range (< 120 K).
    ///
    /// 120 K is a common engineering threshold: below it, most gases liquefy
    /// and materials enter cryogenic behaviour.
    public boolean isCryogenic() {
        return kelvin < 120.0;
    }

    /// Returns the difference between this temperature and `other`.
    ///
    /// Result is always non-negative (absolute difference).
    public double differenceFrom(Temperature other) {
        return Math.abs(this.kelvin - other.kelvin);
    }

    @Override
    public String toString() {
        return "Temperature(" + kelvin + " K)";
    }
}
