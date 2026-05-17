package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedDouble;
import io.github.dfa1.refinedtype.examples.Distance.Unit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class DistanceTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = Distance.ZERO;

        // When
        double result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void positiveAccepted() {
        // Given
        var sut = new Distance(1_234.5);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(1_234.5);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> new Distance(-1.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nanRejected() {
        // When / Then
        assertThatThrownBy(() -> new Distance(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void infinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Distance(Double.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── factory + accessor ──────────────────────────────────────────────────

    @Test
    void ofMetersExposesValue() {
        // Given / When
        Distance result = Distance.of(500, Unit.M);

        // Then
        assertThat(result.to(Unit.M)).isEqualTo(500.0);
    }

    @Test
    void ofKilometersMultipliesByThousand() {
        // Given / When
        Distance result = Distance.of(3, Unit.KM);

        // Then
        assertThat(result.to(Unit.M)).isEqualTo(3_000.0);
    }

    @Test
    void ofMilesUsesInternationalMile() {
        // Given / When
        Distance result = Distance.of(1, Unit.MILE);

        // Then
        assertThat(result.to(Unit.M)).isEqualTo(1_609.344);
    }

    @Test
    void ofNauticalMilesUsesEighteenFiftyTwo() {
        // Given / When
        Distance result = Distance.of(1, Unit.NAUTICAL_MILE);

        // Then
        assertThat(result.to(Unit.M)).isEqualTo(1_852.0);
    }

    @Test
    void toKilometers() {
        // Given
        var sut = Distance.of(2_500, Unit.M);

        // When
        double result = sut.to(Unit.KM);

        // Then
        assertThat(result).isEqualTo(2.5);
    }

    @Test
    void roundTripsThroughKilometers() {
        // Given
        var sut = Distance.of(7.5, Unit.KM);

        // When
        double result = sut.to(Unit.KM);

        // Then
        assertThat(result).isCloseTo(7.5, within(1e-12));
    }

    // ── unit enum ───────────────────────────────────────────────────────────

    @Test
    void unitMetersMatchKnownConstants() {
        // When / Then
        assertThat(Unit.M.meters).isEqualTo(1.0);
        assertThat(Unit.KM.meters).isEqualTo(1_000.0);
        assertThat(Unit.MILE.meters).isEqualTo(1_609.344);
        assertThat(Unit.NAUTICAL_MILE.meters).isEqualTo(1_852.0);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenShorter() {
        // Given
        var sut = Distance.of(100, Unit.M);
        var other = Distance.of(1, Unit.KM);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = Distance.of(1, Unit.KM);
        var other = Distance.of(1_000, Unit.M);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesUnit() {
        // Given
        var sut = Distance.of(42, Unit.M);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Distance(42.0 m)");
    }

    // ── RefinedDouble ───────────────────────────────────────────────────────

    @Test
    void implementsRefinedDouble() {
        // Given
        RefinedDouble<Distance> sut = Distance.of(7, Unit.M);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(7.0);
    }
}
