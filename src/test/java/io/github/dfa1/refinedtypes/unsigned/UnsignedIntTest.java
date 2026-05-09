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

    // ── arithmetic ───────────────────────────────────────────────────────────

    @Test
    void addSimple() {
        // Given
        var sut = new UnsignedInt(3_000_000_000L);

        // When
        UnsignedInt result = sut.add(new UnsignedInt(1_000_000_000L));

        // Then
        assertThat(result.value()).isEqualTo(4_000_000_000L);
    }

    @Test
    void addWrapsAroundMax() {
        // MAX + 1 must wrap to 0
        // Given
        var sut = new UnsignedInt(UnsignedInt.MAX_VALUE);

        // When
        UnsignedInt result = sut.add(new UnsignedInt(1L));

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void addIdentity() {
        // Given
        var sut = new UnsignedInt(0xDEAD_BEEFL);

        // When
        UnsignedInt result = sut.add(new UnsignedInt(0L));

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
    }

    @Test
    void subSimple() {
        // Given
        var sut = new UnsignedInt(5L);

        // When
        UnsignedInt result = sut.sub(new UnsignedInt(3L));

        // Then
        assertThat(result.value()).isEqualTo(2L);
    }

    @Test
    void subWrapsAroundZero() {
        // 0 - 1 must wrap to MAX
        // Given
        var sut = new UnsignedInt(0L);

        // When
        UnsignedInt result = sut.sub(new UnsignedInt(1L));

        // Then
        assertThat(result.value()).isEqualTo(UnsignedInt.MAX_VALUE);
    }

    @Test
    void subSelf() {
        // a - a = 0 for any a
        // Given
        var sut = new UnsignedInt(3_000_000_000L);

        // When
        UnsignedInt result = sut.sub(sut);

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void mulSimple() {
        // Given
        var sut = new UnsignedInt(1_000_000L);

        // When
        UnsignedInt result = sut.mul(new UnsignedInt(1_000L));

        // Then
        assertThat(result.value()).isEqualTo(1_000_000_000L);
    }

    @Test
    void mulWrapsAroundMax() {
        // MAX * 2 = (2^32 - 1) * 2 = 2^33 - 2 → lower 32 bits = MAX - 1 = 4_294_967_294
        // Given
        var sut = new UnsignedInt(UnsignedInt.MAX_VALUE);

        // When
        UnsignedInt result = sut.mul(new UnsignedInt(2L));

        // Then
        assertThat(result.value()).isEqualTo(4_294_967_294L);
    }

    @Test
    void mulByZero() {
        // Given
        var sut = new UnsignedInt(0xDEAD_BEEFL);

        // When
        UnsignedInt result = sut.mul(new UnsignedInt(0L));

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void mulByOne() {
        // Given
        var sut = new UnsignedInt(0xDEAD_BEEFL);

        // When
        UnsignedInt result = sut.mul(new UnsignedInt(1L));

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
    }

    @Test
    void divSimple() {
        // 4_000_000_000 / 3 = 1_333_333_333
        // Given
        var sut = new UnsignedInt(4_000_000_000L);

        // When
        UnsignedInt result = sut.div(new UnsignedInt(3L));

        // Then
        assertThat(result.value()).isEqualTo(1_333_333_333L);
    }

    @Test
    void divAboveSignedMax() {
        // dividend > Integer.MAX_VALUE — signed divide would give wrong answer
        // 3_000_000_000 / 2 = 1_500_000_000
        // Given
        var sut = new UnsignedInt(3_000_000_000L);

        // When
        UnsignedInt result = sut.div(new UnsignedInt(2L));

        // Then
        assertThat(result.value()).isEqualTo(1_500_000_000L);
    }

    @Test
    void divByZeroThrows() {
        // Given
        var sut = new UnsignedInt(1L);

        // When / Then
        assertThatThrownBy(() -> sut.div(new UnsignedInt(0L)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void remSimple() {
        // 4_000_000_000 % 3 = 1
        // Given
        var sut = new UnsignedInt(4_000_000_000L);

        // When
        UnsignedInt result = sut.rem(new UnsignedInt(3L));

        // Then
        assertThat(result.value()).isEqualTo(1L);
    }

    @Test
    void remByZeroThrows() {
        // Given
        var sut = new UnsignedInt(1L);

        // When / Then
        assertThatThrownBy(() -> sut.rem(new UnsignedInt(0L)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void divRemRelationship() {
        // a = (a / b) * b + (a % b)  for all b != 0
        // Given
        var sut = new UnsignedInt(4_000_000_000L);
        var divisor = new UnsignedInt(7L);

        // When
        UnsignedInt q = sut.div(divisor);
        UnsignedInt r = sut.rem(divisor);
        UnsignedInt result = q.mul(divisor).add(r);

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
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
