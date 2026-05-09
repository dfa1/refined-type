package io.github.dfa1.refinedtypes.unsigned;

import io.github.dfa1.refinedtypes.unsigned.UnsignedInt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnsignedIntTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = new UnsignedInt(0L);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxValueIsValid() {
        // Given
        var sut = new UnsignedInt(UnsignedInt.MAX_VALUE);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isEqualTo(4_294_967_295L);
    }

    @Test
    void negativeRejected() {
        // Given
        long input = -1L;

        // When / Then
        assertThatThrownBy(() -> new UnsignedInt(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aboveMaxRejected() {
        // Given
        long input = 4_294_967_296L;

        // When / Then
        assertThatThrownBy(() -> new UnsignedInt(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── unsigned bit-pattern semantics ──────────────────────────────────────

    @Test
    void valueAboveSignedMaxRecoveredCorrectly() {
        // 2^31 = 2_147_483_648 — raw bits are Integer.MIN_VALUE (negative signed),
        // but Integer.toUnsignedLong must recover the correct unsigned value.
        // Given
        var sut = new UnsignedInt(2_147_483_648L);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isEqualTo(2_147_483_648L);
        assertThat(sut.rawBits()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void valuePreservesAll32Bits() {
        // 0xDEADBEEF = 3_735_928_559
        // Given
        var sut = new UnsignedInt(0xDEAD_BEEFL);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isEqualTo(3_735_928_559L);
        assertThat(Integer.toUnsignedString(sut.rawBits())).isEqualTo("3735928559");
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsPositiveWhenUnsignedGreater() {
        // 2_147_483_648 > 1 in unsigned space even though raw bits are negative
        // Given
        var sut = new UnsignedInt(2_147_483_648L);
        var other = new UnsignedInt(1L);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsNegativeWhenUnsignedSmaller() {
        // Given
        var sut = new UnsignedInt(1L);
        var other = new UnsignedInt(2_147_483_648L);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new UnsignedInt(2_147_483_648L);
        var other = new UnsignedInt(2_147_483_648L);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxIsGreaterThanSignedMax() {
        // Given
        var sut = new UnsignedInt(UnsignedInt.MAX_VALUE);
        var signedMax = new UnsignedInt(Integer.MAX_VALUE);

        // When
        int result = sut.compareTo(signedMax);

        // Then
        assertThat(result).isPositive();
    }

    // ── Integer unsigned API ─────────────────────────────────────────────────

    @Test
    void unsignedDivide() {
        // 4_000_000_000 / 3 = 1_333_333_333
        // Given
        var sut = new UnsignedInt(4_000_000_000L);
        var divisor = new UnsignedInt(3L);

        // When
        int result = Integer.divideUnsigned(sut.rawBits(), divisor.rawBits());

        // Then
        assertThat(Integer.toUnsignedLong(result)).isEqualTo(1_333_333_333L);
    }

    @Test
    void unsignedRemainder() {
        // 4_000_000_000 % 3 = 1
        // Given
        var sut = new UnsignedInt(4_000_000_000L);
        var divisor = new UnsignedInt(3L);

        // When
        int result = Integer.remainderUnsigned(sut.rawBits(), divisor.rawBits());

        // Then
        assertThat(Integer.toUnsignedLong(result)).isEqualTo(1L);
    }

    @Test
    void parseUnsignedStringRoundTrips() {
        // Given
        var sut = new UnsignedInt(4_294_967_295L);

        // When
        String result = Integer.toUnsignedString(sut.rawBits());

        // Then
        assertThat(result).isEqualTo("4294967295");
        assertThat(Integer.parseUnsignedInt(result)).isEqualTo(sut.rawBits());
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringShowsUnsignedRepresentation() {
        // Given
        var sut = new UnsignedInt(UnsignedInt.MAX_VALUE);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("UnsignedInt(4294967295)");
    }
}
