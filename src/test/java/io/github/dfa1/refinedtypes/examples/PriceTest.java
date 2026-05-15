package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedFloat;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void positiveValueIsValid() {
        // Given
        var sut = new Price(123.45f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(123.45f);
    }

    @Test
    void tinyPositiveValueIsValid() {
        // Given
        var sut = new Price(Float.MIN_VALUE);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(Float.MIN_VALUE);
    }

    @Test
    void zeroRejected() {
        // When / Then
        assertThatThrownBy(() -> new Price(0f))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> new Price(-0.01f))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nanRejected() {
        // When / Then
        assertThatThrownBy(() -> new Price(Float.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void positiveInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Price(Float.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Price(Float.NEGATIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── notional ────────────────────────────────────────────────────────────

    @Test
    void notionalMultipliesPriceByVolume() {
        // Given
        var sut = new Price(10f);
        var volume = new Volume(3f);

        // When
        float result = sut.notional(volume);

        // Then
        assertThat(result).isEqualTo(30f);
    }

    @Test
    void notionalWithZeroVolumeIsZero() {
        // Given
        var sut = new Price(99f);
        var volume = new Volume(0f);

        // When
        float result = sut.notional(volume);

        // Then
        assertThat(result).isZero();
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = new Price(1f);
        var other = new Price(2f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new Price(42f);
        var other = new Price(42f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = new Price(7.5f);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Price(7.5)");
    }

    // ── RefinedFloat ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedFloat() {
        // Given
        RefinedFloat sut = new Price(11f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(11f);
    }
}
