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
        var sut = new Percentage(0f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void hundredIsValid() {
        // Given
        var sut = new Percentage(100f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(100f);
    }

    @Test
    void midRangeIsValid() {
        // Given
        var sut = new Percentage(42.5f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(42.5f);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> new Percentage(-0.0001f))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aboveHundredRejected() {
        // When / Then
        assertThatThrownBy(() -> new Percentage(100.0001f))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nanRejected() {
        // When / Then
        assertThatThrownBy(() -> new Percentage(Float.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void positiveInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Percentage(Float.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Percentage(Float.NEGATIVE_INFINITY))
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
        var input = new Probability(0.25f);

        // When
        Percentage result = Percentage.ofProbability(input);

        // Then
        assertThat(result.value()).isEqualTo(25f);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = new Percentage(10f);
        var other = new Percentage(90f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesPercentSign() {
        // Given
        var sut = new Percentage(42f);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Percentage(42.0%)");
    }

    // ── RefinedFloat ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedFloat() {
        // Given
        RefinedFloat sut = new Percentage(33f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(33f);
    }
}
