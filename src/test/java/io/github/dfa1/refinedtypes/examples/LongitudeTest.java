package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedDouble;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class LongitudeTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void primeMeridianIsZero() {
        // Given
        var sut = Longitude.ZERO;

        // When
        double result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void eastBoundaryAccepted() {
        // Given
        var sut = new Longitude(180.0);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(180.0);
    }

    @Test
    void westBoundaryAccepted() {
        // Given
        var sut = new Longitude(-180.0);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(-180.0);
    }

    @Test
    void typicalLongitudeAccepted() {
        // Given
        var sut = new Longitude(-73.9857);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(-73.9857);
    }

    @Test
    void aboveMaxRejected() {
        // When / Then
        assertThatThrownBy(() -> new Longitude(180.0001))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void belowMinRejected() {
        // When / Then
        assertThatThrownBy(() -> new Longitude(-180.0001))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nanRejected() {
        // When / Then
        assertThatThrownBy(() -> new Longitude(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Longitude(Double.NEGATIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = new Longitude(-122.4194);
        var other = new Longitude(2.3522);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new Longitude(139.6917);
        var other = new Longitude(139.6917);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = new Longitude(-0.1278);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Longitude(-0.1278)");
    }

    // ── hemisphere predicates ───────────────────────────────────────────────

    @Test
    void positiveLongitudeIsEastern() {
        // Given
        var sut = new Longitude(139.6917); // Tokyo

        // When / Then
        assertThat(sut.isEasternHemisphere()).isTrue();
        assertThat(sut.isWesternHemisphere()).isFalse();
        assertThat(sut.isPrimeMeridian()).isFalse();
    }

    @Test
    void negativeLongitudeIsWestern() {
        // Given
        var sut = new Longitude(-74.0060); // NYC

        // When / Then
        assertThat(sut.isWesternHemisphere()).isTrue();
        assertThat(sut.isEasternHemisphere()).isFalse();
        assertThat(sut.isPrimeMeridian()).isFalse();
    }

    @Test
    void zeroIsPrimeMeridian() {
        // Given
        var sut = Longitude.ZERO;

        // When / Then
        assertThat(sut.isPrimeMeridian()).isTrue();
        assertThat(sut.isEasternHemisphere()).isFalse();
        assertThat(sut.isWesternHemisphere()).isFalse();
    }

    // ── toRadians ───────────────────────────────────────────────────────────

    @Test
    void toRadiansZero() {
        // Given
        var sut = Longitude.ZERO;

        // When
        double result = sut.toRadians();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void toRadiansOneEightyIsPi() {
        // Given
        var sut = new Longitude(180.0);

        // When
        double result = sut.toRadians();

        // Then
        assertThat(result).isCloseTo(Math.PI, within(1e-12));
    }

    // ── RefinedDouble ───────────────────────────────────────────────────────

    @Test
    void implementsRefinedDouble() {
        // Given
        RefinedDouble sut = new Longitude(56.78);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(56.78);
    }
}
