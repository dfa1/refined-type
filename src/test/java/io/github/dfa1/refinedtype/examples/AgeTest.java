package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedShort;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgeTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = new Age(0);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void typicalAgeIsValid() {
        // Given
        var sut = new Age(42);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(42);
    }

    @Test
    void maxIsValid() {
        // Given
        var sut = new Age(Age.MAX);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(150);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> new Age(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aboveMaxRejected() {
        // When / Then
        assertThatThrownBy(() -> new Age(Age.MAX + 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = new Age(20);
        var other = new Age(30);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new Age(50);
        var other = new Age(50);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = new Age(33);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Age(33)");
    }

    // ── RefinedShort ─────────────────────────────────────────────────────────

    @Test
    void implementsRefinedShort() {
        // Given
        RefinedShort<?> sut = new Age(18);

        // When
        short result = sut.value();

        // Then
        assertThat(result).isEqualTo((short) 18);
    }
}
