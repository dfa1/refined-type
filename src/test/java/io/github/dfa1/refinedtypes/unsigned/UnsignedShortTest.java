package io.github.dfa1.refinedtypes.unsigned;

import io.github.dfa1.refinedtypes.unsigned.UnsignedShort;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnsignedShortTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = new UnsignedShort(0);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxValueIsValid() {
        // Given
        var sut = new UnsignedShort(UnsignedShort.MAX_VALUE);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(65_535);
    }

    @Test
    void negativeRejected() {
        // Given
        int input = -1;

        // When / Then
        assertThatThrownBy(() -> new UnsignedShort(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aboveMaxRejected() {
        // Given
        int input = 65_536;

        // When / Then
        assertThatThrownBy(() -> new UnsignedShort(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── unsigned bit-pattern semantics ──────────────────────────────────────

    @Test
    void valueAboveSignedMaxRecoveredCorrectly() {
        // 2^15 = 32_768 — raw bits are Short.MIN_VALUE (negative signed),
        // but Short.toUnsignedInt must recover the correct unsigned value.
        // Given
        var sut = new UnsignedShort(32_768);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(32_768);
        assertThat(sut.rawBits()).isEqualTo(Short.MIN_VALUE);
    }

    @Test
    void valuePreservesAll16Bits() {
        // 0xBEEF = 48_879
        // Given
        var sut = new UnsignedShort(0xBEEF);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(48_879);
        assertThat(Short.toUnsignedInt(sut.rawBits())).isEqualTo(48_879);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsPositiveWhenUnsignedGreater() {
        // 32_768 > 1 in unsigned space even though raw bits are negative
        // Given
        var sut = new UnsignedShort(32_768);
        var other = new UnsignedShort(1);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsNegativeWhenUnsignedSmaller() {
        // Given
        var sut = new UnsignedShort(1);
        var other = new UnsignedShort(32_768);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new UnsignedShort(32_768);
        var other = new UnsignedShort(32_768);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxIsGreaterThanSignedMax() {
        // Given
        var sut = new UnsignedShort(UnsignedShort.MAX_VALUE);
        var signedMax = new UnsignedShort(Short.MAX_VALUE);

        // When
        int result = sut.compareTo(signedMax);

        // Then
        assertThat(result).isPositive();
    }

    // ── Short unsigned API ───────────────────────────────────────────────────

    @Test
    void toUnsignedIntRoundTrips() {
        // Given
        var sut = new UnsignedShort(65_535);

        // When
        int result = Short.toUnsignedInt(sut.rawBits());

        // Then
        assertThat(result).isEqualTo(65_535);
    }

    @Test
    void toUnsignedLongRoundTrips() {
        // Given
        var sut = new UnsignedShort(65_535);

        // When
        long result = Short.toUnsignedLong(sut.rawBits());

        // Then
        assertThat(result).isEqualTo(65_535L);
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringShowsUnsignedRepresentation() {
        // Given
        var sut = new UnsignedShort(UnsignedShort.MAX_VALUE);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("UnsignedShort(65535)");
    }

    @Test
    void toStringZero() {
        // Given
        var sut = new UnsignedShort(0);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("UnsignedShort(0)");
    }
}
