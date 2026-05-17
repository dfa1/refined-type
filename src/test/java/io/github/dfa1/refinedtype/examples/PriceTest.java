package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedDouble;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void positiveValueIsValid() {
        // Given
        var sut = new Price(123.45);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(123.45);
    }

    @Test
    void zeroIsValid() {
        // Given
        var sut = new Price(0.0);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void negativeIsValid() {
        // Given
        var sut = new Price(-37.63);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(-37.63);
    }

    @Test
    void nanRejected() {
        // When / Then
        assertThatThrownBy(() -> new Price(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void positiveInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Price(Double.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Price(Double.NEGATIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = new Price(1.0);
        var other = new Price(2.0);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new Price(42.0);
        var other = new Price(42.0);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = new Price(7.5);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Price(7.5)");
    }

    // ── RefinedDouble ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedDouble() {
        // Given
        RefinedDouble sut = new Price(11.0);

        // When
        double result = sut.value();

        // Then
        assertThat(result).isEqualTo(11.0);
    }
}
