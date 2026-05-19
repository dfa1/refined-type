package io.github.dfa1.refinedtype.examples;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class TemperatureTest {

    // --- construction ---

    @Test
    void absoluteZeroAccepted() {
        // Given / When
        var sut = Temperature.of(0.0);

        // Then
        assertThat(sut.value()).isEqualTo(0.0);
    }

    @Test
    void positiveKelvinAccepted() {
        // Given / When
        var sut = Temperature.of(300.0);

        // Then
        assertThat(sut.value()).isEqualTo(300.0);
    }

    @Test
    void negativeKelvinRejected() {
        // Given / When / Then
        assertThatThrownBy(() -> Temperature.of(-1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("absolute zero");
    }

    @Test
    void nanRejected() {
        assertThatThrownBy(() -> Temperature.of(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("finite");
    }

    @Test
    void infinityRejected() {
        assertThatThrownBy(() -> Temperature.of(Double.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("finite");
    }

    // --- factory ---

    @Test
    void ofCelsiusConvertsToKelvin() {
        // Given / When
        var sut = Temperature.of(0.0, Temperature.Unit.CELSIUS);

        // Then
        assertThat(sut.value()).isCloseTo(273.15, within(1e-9));
    }

    @Test
    void ofFahrenheitConvertsToKelvin() {
        // Given / When
        var sut = Temperature.of(32.0, Temperature.Unit.FAHRENHEIT);

        // Then
        assertThat(sut.value()).isCloseTo(273.15, within(1e-9));
    }

    @Test
    void ofKelvinIsIdentity() {
        // Given / When
        var sut = Temperature.of(300.0, Temperature.Unit.KELVIN);

        // Then
        assertThat(sut.value()).isEqualTo(300.0);
    }

    @Test
    void celsiusAbsoluteZeroRejected() {
        assertThatThrownBy(() -> Temperature.of(-274.0, Temperature.Unit.CELSIUS))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- conversions ---

    @Test
    void toCelsius() {
        // Given
        var sut = Temperature.WATER_BOILING;

        // When
        double result = sut.to(Temperature.Unit.CELSIUS);

        // Then
        assertThat(result).isCloseTo(100.0, within(1e-9));
    }

    @Test
    void toFahrenheit() {
        // Given
        var sut = Temperature.WATER_FREEZING;

        // When
        double result = sut.to(Temperature.Unit.FAHRENHEIT);

        // Then
        assertThat(result).isCloseTo(32.0, within(1e-9));
    }

    @Test
    void bodyTemperatureToCelsius() {
        // Given
        var sut = Temperature.BODY;

        // When
        double result = sut.to(Temperature.Unit.CELSIUS);

        // Then
        assertThat(result).isCloseTo(37.0, within(1e-9));
    }

    @Test
    void bodyTemperatureToFahrenheit() {
        // Given
        var sut = Temperature.BODY;

        // When
        double result = sut.to(Temperature.Unit.FAHRENHEIT);

        // Then
        assertThat(result).isCloseTo(98.6, within(1e-9));
    }

    @Test
    void roundTripCelsius() {
        // Given
        double original = 42.5;

        // When
        double result = Temperature.of(original, Temperature.Unit.CELSIUS)
                .to(Temperature.Unit.CELSIUS);

        // Then
        assertThat(result).isCloseTo(original, within(1e-9));
    }

    @Test
    void roundTripFahrenheit() {
        // Given
        double original = 98.6;

        // When
        double result = Temperature.of(original, Temperature.Unit.FAHRENHEIT)
                .to(Temperature.Unit.FAHRENHEIT);

        // Then
        assertThat(result).isCloseTo(original, within(1e-9));
    }

    // --- predicates ---

    @Test
    void absoluteZeroIsAbsoluteZero() {
        assertThat(Temperature.ABSOLUTE_ZERO.isAbsoluteZero()).isTrue();
    }

    @Test
    void nonZeroIsNotAbsoluteZero() {
        assertThat(Temperature.WATER_FREEZING.isAbsoluteZero()).isFalse();
    }

    @Test
    void waterFreezingIsFreezing() {
        assertThat(Temperature.WATER_FREEZING.isFreezing()).isTrue();
    }

    @Test
    void aboveFreezingIsNotFreezing() {
        assertThat(Temperature.STANDARD.isFreezing()).isFalse();
    }

    @Test
    void waterBoilingIsBoiling() {
        assertThat(Temperature.WATER_BOILING.isBoiling()).isTrue();
    }

    @Test
    void belowBoilingIsNotBoiling() {
        assertThat(Temperature.STANDARD.isBoiling()).isFalse();
    }

    @Test
    void liquidNitrogenIsCryogenic() {
        // Given: liquid nitrogen boils at 77 K
        var sut = Temperature.of(77.0);

        // When / Then
        assertThat(sut.isCryogenic()).isTrue();
    }

    @Test
    void roomTemperatureIsNotCryogenic() {
        assertThat(Temperature.STANDARD.isCryogenic()).isFalse();
    }

    // --- differenceFrom ---

    @Test
    void differenceFromSelf() {
        // Given
        var sut = Temperature.STANDARD;

        // When
        double result = sut.differenceFrom(Temperature.STANDARD);

        // Then
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    void differenceFromIsSymmetric() {
        // Given
        var a = Temperature.WATER_FREEZING;
        var b = Temperature.WATER_BOILING;

        // When / Then
        assertThat(a.differenceFrom(b)).isCloseTo(b.differenceFrom(a), within(1e-9));
    }

    @Test
    void differenceFreezingToBoiling() {
        // Given
        var sut = Temperature.WATER_FREEZING;

        // When
        double result = sut.differenceFrom(Temperature.WATER_BOILING);

        // Then
        assertThat(result).isCloseTo(100.0, within(1e-9));
    }

    // --- constants ---

    @Test
    void standardIs20Celsius() {
        assertThat(Temperature.STANDARD.to(Temperature.Unit.CELSIUS)).isCloseTo(20.0, within(1e-9));
    }

    // --- comparison ---

    @Test
    void compareToOrdering() {
        // Given
        var cold = Temperature.WATER_FREEZING;
        var hot  = Temperature.WATER_BOILING;

        // When / Then
        assertThat(cold.compareTo(hot)).isLessThan(0);
        assertThat(hot.compareTo(cold)).isGreaterThan(0);
        assertThat(cold.compareTo(cold)).isEqualTo(0);
    }
}
