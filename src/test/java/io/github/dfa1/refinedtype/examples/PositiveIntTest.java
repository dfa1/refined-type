package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedInt;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositiveIntTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void oneIsValid() {
        // Given
        var sut = new PositiveInt(1);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void largeValueIsValid() {
        // Given
        var sut = new PositiveInt(Integer.MAX_VALUE);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void zeroRejected() {
        // When / Then
        assertThatThrownBy(() -> new PositiveInt(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("0");
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> new PositiveInt(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-1");
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = new PositiveInt(1);
        var other = new PositiveInt(2);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new PositiveInt(42);
        var other = new PositiveInt(42);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void compareToReturnsPositiveWhenLarger() {
        // Given
        var sut = new PositiveInt(10);
        var other = new PositiveInt(5);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = new PositiveInt(7);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("PositiveInt(7)");
    }

    // ── RefinedInt ──────────────────────────────────────────────────────────

    @Test
    void implementsRefinedInt() {
        // Given
        RefinedInt<?> sut = new PositiveInt(3);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(3);
    }
}
