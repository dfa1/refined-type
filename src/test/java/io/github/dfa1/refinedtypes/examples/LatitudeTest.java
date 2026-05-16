package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedDouble;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LatitudeTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void equatorIsZero() {
        // Given
        var sut = Latitude.ZERO;

        // When
        double result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void northPoleIsNinety() {
        // Given
        var sut = Latitude.NORTH_POLE;

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(90.0);
    }

    @Test
    void southPoleIsNegativeNinety() {
        // Given
        var sut = Latitude.SOUTH_POLE;

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(-90.0);
    }

    @Test
    void typicalLatitudeAccepted() {
        // Given
        var sut = new Latitude(45.4642);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(45.4642);
    }

    @Test
    void aboveMaxRejected() {
        // When / Then
        assertThatThrownBy(() -> new Latitude(90.0001))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void belowMinRejected() {
        // When / Then
        assertThatThrownBy(() -> new Latitude(-90.0001))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nanRejected() {
        // When / Then
        assertThatThrownBy(() -> new Latitude(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void positiveInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Latitude(Double.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = new Latitude(0.0);
        var other = new Latitude(45.0);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new Latitude(51.5074);
        var other = new Latitude(51.5074);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = new Latitude(40.7128);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Latitude(40.7128)");
    }

    // ── RefinedDouble ───────────────────────────────────────────────────────

    @Test
    void implementsRefinedDouble() {
        // Given
        RefinedDouble sut = new Latitude(12.34);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(12.34);
    }
}
