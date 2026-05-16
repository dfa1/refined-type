package io.github.dfa1.refinedtypes.unsigned;

import io.github.dfa1.refinedtypes.unsigned.UnsignedInt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnsignedIntTest {

    // ── fromString ───────────────────────────────────────────────────────────

    @Test
    void fromStringZero() {
        // Given / When
        UnsignedInt result = UnsignedInt.fromString("0");

        // Then
        assertThat(result.asLong()).isZero();
    }

    @Test
    void fromStringMaxValue() {
        // Given / When
        UnsignedInt result = UnsignedInt.fromString("4294967295");

        // Then
        assertThat(result.asLong()).isEqualTo(4_294_967_295L);
    }

    @Test
    void fromStringAboveMaxThrows() {
        // Given / When / Then
        assertThatThrownBy(() -> UnsignedInt.fromString("4294967296"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = new UnsignedInt(0L);

        // When
        long result = sut.asLong();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxValueIsValid() {
        // Given
        var sut = new UnsignedInt(UnsignedInt.MAX_VALUE);

        // When
        long result = sut.asLong();

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
        // but asLong() must recover the correct unsigned value.
        // Given
        var sut = new UnsignedInt(2_147_483_648L);

        // When
        long result = sut.asLong();

        // Then
        assertThat(result).isEqualTo(2_147_483_648L);
        assertThat(sut.value()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void valuePreservesAll32Bits() {
        // 0xDEADBEEF = 3_735_928_559
        // Given
        var sut = new UnsignedInt(0xDEAD_BEEFL);

        // When
        long result = sut.asLong();

        // Then
        assertThat(result).isEqualTo(3_735_928_559L);
        assertThat(Integer.toUnsignedString(sut.value())).isEqualTo("3735928559");
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
        assertThat(result.asLong()).isEqualTo(4_000_000_000L);
    }

    @Test
    void addWrapsAroundMax() {
        // MAX + 1 must wrap to 0
        // Given
        var sut = new UnsignedInt(UnsignedInt.MAX_VALUE);

        // When
        UnsignedInt result = sut.add(new UnsignedInt(1L));

        // Then
        assertThat(result.asLong()).isZero();
    }

    @Test
    void addIdentity() {
        // Given
        var sut = new UnsignedInt(0xDEAD_BEEFL);

        // When
        UnsignedInt result = sut.add(new UnsignedInt(0L));

        // Then
        assertThat(result.asLong()).isEqualTo(sut.asLong());
    }

    @Test
    void subtractSimple() {
        // Given
        var sut = new UnsignedInt(5L);

        // When
        UnsignedInt result = sut.subtract(new UnsignedInt(3L));

        // Then
        assertThat(result.asLong()).isEqualTo(2L);
    }

    @Test
    void subtractWrapsAroundZero() {
        // 0 - 1 must wrap to MAX
        // Given
        var sut = new UnsignedInt(0L);

        // When
        UnsignedInt result = sut.subtract(new UnsignedInt(1L));

        // Then
        assertThat(result.asLong()).isEqualTo(UnsignedInt.MAX_VALUE);
    }

    @Test
    void subtractSelf() {
        // a - a = 0 for any a
        // Given
        var sut = new UnsignedInt(3_000_000_000L);

        // When
        UnsignedInt result = sut.subtract(sut);

        // Then
        assertThat(result.asLong()).isZero();
    }

    @Test
    void multiplySimple() {
        // Given
        var sut = new UnsignedInt(1_000_000L);

        // When
        UnsignedInt result = sut.multiply(new UnsignedInt(1_000L));

        // Then
        assertThat(result.asLong()).isEqualTo(1_000_000_000L);
    }

    @Test
    void multiplyWrapsAroundMax() {
        // MAX * 2 = (2^32 - 1) * 2 = 2^33 - 2 → lower 32 bits = MAX - 1 = 4_294_967_294
        // Given
        var sut = new UnsignedInt(UnsignedInt.MAX_VALUE);

        // When
        UnsignedInt result = sut.multiply(new UnsignedInt(2L));

        // Then
        assertThat(result.asLong()).isEqualTo(4_294_967_294L);
    }

    @Test
    void multiplyByZero() {
        // Given
        var sut = new UnsignedInt(0xDEAD_BEEFL);

        // When
        UnsignedInt result = sut.multiply(new UnsignedInt(0L));

        // Then
        assertThat(result.asLong()).isZero();
    }

    @Test
    void multiplyByOne() {
        // Given
        var sut = new UnsignedInt(0xDEAD_BEEFL);

        // When
        UnsignedInt result = sut.multiply(new UnsignedInt(1L));

        // Then
        assertThat(result.asLong()).isEqualTo(sut.asLong());
    }

    @Test
    void divideSimple() {
        // 4_000_000_000 / 3 = 1_333_333_333
        // Given
        var sut = new UnsignedInt(4_000_000_000L);

        // When
        UnsignedInt result = sut.divide(new UnsignedInt(3L));

        // Then
        assertThat(result.asLong()).isEqualTo(1_333_333_333L);
    }

    @Test
    void divideAboveSignedMax() {
        // dividend > Integer.MAX_VALUE — signed divide would give wrong answer
        // 3_000_000_000 / 2 = 1_500_000_000
        // Given
        var sut = new UnsignedInt(3_000_000_000L);

        // When
        UnsignedInt result = sut.divide(new UnsignedInt(2L));

        // Then
        assertThat(result.asLong()).isEqualTo(1_500_000_000L);
    }

    @Test
    void divideByZeroThrows() {
        // Given
        var sut = new UnsignedInt(1L);

        // When / Then
        assertThatThrownBy(() -> sut.divide(new UnsignedInt(0L)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void remainderSimple() {
        // 4_000_000_000 % 3 = 1
        // Given
        var sut = new UnsignedInt(4_000_000_000L);

        // When
        UnsignedInt result = sut.remainder(new UnsignedInt(3L));

        // Then
        assertThat(result.asLong()).isEqualTo(1L);
    }

    @Test
    void remainderByZeroThrows() {
        // Given
        var sut = new UnsignedInt(1L);

        // When / Then
        assertThatThrownBy(() -> sut.remainder(new UnsignedInt(0L)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void divideRemainderRelationship() {
        // a = (a / b) * b + (a % b)  for all b != 0
        // Given
        var sut = new UnsignedInt(4_000_000_000L);
        var divisor = new UnsignedInt(7L);

        // When
        UnsignedInt q = sut.divide(divisor);
        UnsignedInt r = sut.remainder(divisor);
        UnsignedInt result = q.multiply(divisor).add(r);

        // Then
        assertThat(result.asLong()).isEqualTo(sut.asLong());
    }

    @Test
    void parseUnsignedStringRoundTrips() {
        // Given
        var sut = new UnsignedInt(4_294_967_295L);

        // When
        String result = Integer.toUnsignedString(sut.value());

        // Then
        assertThat(result).isEqualTo("4294967295");
        assertThat(Integer.parseUnsignedInt(result)).isEqualTo(sut.value());
    }

    // ── widening ─────────────────────────────────────────────────────────────

    @Test
    void toUnsignedLongPreservesValue() {
        // Given
        var sut = new UnsignedInt(UnsignedInt.MAX_VALUE);

        // When
        UnsignedLong result = sut.toUnsignedLong();

        // Then
        assertThat(Long.toUnsignedString(result.rawBits())).isEqualTo("4294967295");
    }

    @Test
    void wideningEnablesCrossTypeArithmetic() {
        // UnsignedInt + UnsignedLong — widen first, then add
        // Given
        var sut = new UnsignedInt(4_000_000_000L);
        var other = UnsignedLong.fromString("9999999999999999999");

        // When
        UnsignedLong result = sut.toUnsignedLong().add(other);

        // Then
        assertThat(Long.toUnsignedString(result.rawBits())).isEqualTo("10000000003999999999");
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
