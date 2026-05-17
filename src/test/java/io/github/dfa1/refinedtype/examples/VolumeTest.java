package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedFloat;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VolumeTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = new Volume(0f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void positiveValueIsValid() {
        // Given
        var sut = new Volume(1_000_000f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(1_000_000f);
    }

    @Test
    void fractionalValueIsValid() {
        // Given
        var sut = new Volume(0.125f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(0.125f);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> new Volume(-1f))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nanRejected() {
        // When / Then
        assertThatThrownBy(() -> new Volume(Float.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void positiveInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Volume(Float.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> new Volume(Float.NEGATIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── constants ───────────────────────────────────────────────────────────

    @Test
    void zeroConstant() {
        // Given / When
        Volume result = Volume.ZERO;

        // Then
        assertThat(result.value()).isZero();
    }

    // ── plus ────────────────────────────────────────────────────────────────

    @Test
    void plusAddsValues() {
        // Given
        var sut = new Volume(100f);
        var other = new Volume(250f);

        // When
        Volume result = sut.plus(other);

        // Then
        assertThat(result.value()).isEqualTo(350f);
    }

    @Test
    void plusZeroIsIdentity() {
        // Given
        var sut = new Volume(42f);

        // When
        Volume result = sut.plus(Volume.ZERO);

        // Then
        assertThat(result.value()).isEqualTo(42f);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = new Volume(1f);
        var other = new Volume(2f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new Volume(42f);
        var other = new Volume(42f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = new Volume(10f);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Volume(10.0)");
    }

    // ── RefinedFloat ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedFloat() {
        // Given
        RefinedFloat sut = new Volume(5f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(5f);
    }
}
