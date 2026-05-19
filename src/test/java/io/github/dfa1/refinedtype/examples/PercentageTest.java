package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedFloat;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PercentageTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = Percentage.of(0f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void hundredIsValid() {
        // Given
        var sut = Percentage.of(100f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(100f);
    }

    @Test
    void midRangeIsValid() {
        // Given
        var sut = Percentage.of(42.5f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(42.5f);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> Percentage.of(-0.0001f))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aboveHundredRejected() {
        // When / Then
        assertThatThrownBy(() -> Percentage.of(100.0001f))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nanRejected() {
        // When / Then
        assertThatThrownBy(() -> Percentage.of(Float.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void positiveInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> Percentage.of(Float.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> Percentage.of(Float.NEGATIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── constants ───────────────────────────────────────────────────────────

    @Test
    void zeroConstant() {
        // Given / When
        Percentage result = Percentage.ZERO;

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void hundredConstant() {
        // Given / When
        Percentage result = Percentage.HUNDRED;

        // Then
        assertThat(result.value()).isEqualTo(100f);
    }

    // ── conversion ──────────────────────────────────────────────────────────

    @Test
    void ofProbabilityScalesByHundred() {
        // Given
        var input = Probability.of(0.25f);

        // When
        Percentage result = Percentage.ofProbability(input);

        // Then
        assertThat(result.value()).isEqualTo(25f);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = Percentage.of(10f);
        var other = Percentage.of(90f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesPercentSign() {
        // Given
        var sut = Percentage.of(42f);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Percentage(42.0%)");
    }

    // ── RefinedFloat ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedFloat() {
        // Given
        RefinedFloat sut = Percentage.of(33f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(33f);
    }
}
