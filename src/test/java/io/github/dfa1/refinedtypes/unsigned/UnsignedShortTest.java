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

    // ── arithmetic ───────────────────────────────────────────────────────────

    @Test
    void addSimple() {
        // Given
        var sut = new UnsignedShort(60_000);

        // When
        UnsignedShort result = sut.add(new UnsignedShort(5_000));

        // Then
        assertThat(result.value()).isEqualTo(65_000);
    }

    @Test
    void addWrapsAroundMax() {
        // MAX + 1 must wrap to 0
        // Given
        var sut = new UnsignedShort(UnsignedShort.MAX_VALUE);

        // When
        UnsignedShort result = sut.add(new UnsignedShort(1));

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void addIdentity() {
        // Given
        var sut = new UnsignedShort(0xBEEF);

        // When
        UnsignedShort result = sut.add(new UnsignedShort(0));

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
    }

    @Test
    void subtractSimple() {
        // Given
        var sut = new UnsignedShort(10);

        // When
        UnsignedShort result = sut.subtract(new UnsignedShort(3));

        // Then
        assertThat(result.value()).isEqualTo(7);
    }

    @Test
    void subtractWrapsAroundZero() {
        // 0 - 1 must wrap to MAX
        // Given
        var sut = new UnsignedShort(0);

        // When
        UnsignedShort result = sut.subtract(new UnsignedShort(1));

        // Then
        assertThat(result.value()).isEqualTo(UnsignedShort.MAX_VALUE);
    }

    @Test
    void subtractSelf() {
        // Given
        var sut = new UnsignedShort(50_000);

        // When
        UnsignedShort result = sut.subtract(sut);

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void multiplySimple() {
        // Given
        var sut = new UnsignedShort(300);

        // When
        UnsignedShort result = sut.multiply(new UnsignedShort(200));

        // Then
        assertThat(result.value()).isEqualTo(60_000);
    }

    @Test
    void multiplyWrapsAroundMax() {
        // MAX * MAX mod 2^16 = (2^16-1)^2 mod 2^16 = 1
        // Given
        var sut = new UnsignedShort(UnsignedShort.MAX_VALUE);

        // When
        UnsignedShort result = sut.multiply(new UnsignedShort(UnsignedShort.MAX_VALUE));

        // Then
        assertThat(result.value()).isEqualTo(1);
    }

    @Test
    void multiplyByZero() {
        // Given
        var sut = new UnsignedShort(0xBEEF);

        // When
        UnsignedShort result = sut.multiply(new UnsignedShort(0));

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void multiplyByOne() {
        // Given
        var sut = new UnsignedShort(0xBEEF);

        // When
        UnsignedShort result = sut.multiply(new UnsignedShort(1));

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
    }

    @Test
    void divideSimple() {
        // Given
        var sut = new UnsignedShort(60_000);

        // When
        UnsignedShort result = sut.divide(new UnsignedShort(3));

        // Then
        assertThat(result.value()).isEqualTo(20_000);
    }

    @Test
    void divideAboveSignedMax() {
        // 40_000 / 2 = 20_000 — value above Short.MAX_VALUE
        // Given
        var sut = new UnsignedShort(40_000);

        // When
        UnsignedShort result = sut.divide(new UnsignedShort(2));

        // Then
        assertThat(result.value()).isEqualTo(20_000);
    }

    @Test
    void divideByZeroThrows() {
        // Given
        var sut = new UnsignedShort(1);

        // When / Then
        assertThatThrownBy(() -> sut.divide(new UnsignedShort(0)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void remainderSimple() {
        // Given
        var sut = new UnsignedShort(65_000);

        // When
        UnsignedShort result = sut.remainder(new UnsignedShort(3));

        // Then
        assertThat(result.value()).isEqualTo(65_000 % 3);
    }

    @Test
    void remainderByZeroThrows() {
        // Given
        var sut = new UnsignedShort(1);

        // When / Then
        assertThatThrownBy(() -> sut.remainder(new UnsignedShort(0)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void divideRemainderRelationship() {
        // a = (a / b) * b + (a % b)  for all b != 0
        // Given
        var sut = new UnsignedShort(65_000);
        var divisor = new UnsignedShort(7);

        // When
        UnsignedShort q = sut.divide(divisor);
        UnsignedShort r = sut.remainder(divisor);
        UnsignedShort result = q.multiply(divisor).add(r);

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
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
