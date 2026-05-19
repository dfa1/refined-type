package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedFloat;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VelocityTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = Velocity.of(0f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void positiveValueIsValid() {
        // Given
        var sut = Velocity.of(299_792_458f); // speed of light m/s

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(299_792_458f);
    }

    @Test
    void negativeRejected() {
        // Given
        float input = -1f;

        // When / Then
        assertThatThrownBy(() -> Velocity.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nanRejected() {
        // When / Then
        assertThatThrownBy(() -> Velocity.of(Float.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void positiveInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> Velocity.of(Float.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> Velocity.of(Float.NEGATIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = Velocity.of(1f);
        var other = Velocity.of(2f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsPositiveWhenGreater() {
        // Given
        var sut = Velocity.of(2f);
        var other = Velocity.of(1f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = Velocity.of(42f);
        var other = Velocity.of(42f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesUnit() {
        // Given
        var sut = Velocity.of(10f);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Velocity(10.0 m/s)");
    }

    // ── RefinedFloat ─────────────────────────────────────────────────────────

    @Test
    void implementsRefinedFloat() {
        // Given
        RefinedFloat sut = Velocity.of(5f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(5f);
    }
}
