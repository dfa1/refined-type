package io.github.dfa1.refinedtype.unsigned;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnsignedLongTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = UnsignedLong.of(0L);

        // When
        long result = sut.rawBits();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxRepresentedAsMinusOne() {
        // 2^64 - 1 has bit pattern -1L in signed long
        // Given
        var sut = UnsignedLong.MAX;

        // When
        long result = sut.rawBits();

        // Then
        assertThat(result).isEqualTo(-1L);
        assertThat(Long.toUnsignedString(result)).isEqualTo("18446744073709551615");
    }

    @Test
    void fromStringParsesAboveSignedMax() {
        // 2^63 = 9_223_372_036_854_775_808 — just above Long.MAX_VALUE
        // Given
        String s = "9223372036854775808";

        // When
        UnsignedLong result = UnsignedLong.fromString(s);

        // Then
        assertThat(Long.toUnsignedString(result.rawBits())).isEqualTo(s);
    }

    @Test
    void fromStringMaxValue() {
        // Given
        String s = "18446744073709551615";

        // When
        UnsignedLong result = UnsignedLong.fromString(s);

        // Then
        assertThat(result.rawBits()).isEqualTo(-1L);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsPositiveWhenUnsignedGreater() {
        // 2^63 > 1 in unsigned space even though raw bits are negative signed
        // Given
        var sut = UnsignedLong.fromString("9223372036854775808");
        var other = UnsignedLong.of(1L);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = UnsignedLong.of(42L);
        var other = UnsignedLong.of(42L);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxIsGreaterThanSignedMax() {
        // Given
        var sut = UnsignedLong.MAX;
        var signedMax = UnsignedLong.of(Long.MAX_VALUE);

        // When
        int result = sut.compareTo(signedMax);

        // Then
        assertThat(result).isPositive();
    }

    // ── arithmetic ───────────────────────────────────────────────────────────

    @Test
    void addSimple() {
        // Given
        var sut = UnsignedLong.of(Long.MAX_VALUE);

        // When
        UnsignedLong result = sut.add(UnsignedLong.of(1L));

        // Then — 2^63, still a valid unsigned value
        assertThat(Long.toUnsignedString(result.rawBits())).isEqualTo("9223372036854775808");
    }

    @Test
    void addWrapsAroundMax() {
        // MAX + 1 must wrap to ZERO
        // Given
        var sut = UnsignedLong.MAX;

        // When
        UnsignedLong result = sut.add(UnsignedLong.of(1L));

        // Then
        assertThat(result.rawBits()).isZero();
    }

    @Test
    void addIdentity() {
        // Given
        var sut = UnsignedLong.fromString("12345678901234567890");

        // When
        UnsignedLong result = sut.add(UnsignedLong.ZERO);

        // Then
        assertThat(result.rawBits()).isEqualTo(sut.rawBits());
    }

    @Test
    void subtractSimple() {
        // Given
        var sut = UnsignedLong.of(5L);

        // When
        UnsignedLong result = sut.subtract(UnsignedLong.of(3L));

        // Then
        assertThat(result.rawBits()).isEqualTo(2L);
    }

    @Test
    void subtractWrapsAroundZero() {
        // 0 - 1 must wrap to MAX
        // Given
        var sut = UnsignedLong.ZERO;

        // When
        UnsignedLong result = sut.subtract(UnsignedLong.of(1L));

        // Then
        assertThat(result.rawBits()).isEqualTo(UnsignedLong.MAX.rawBits());
    }

    @Test
    void subtractSelf() {
        // Given
        var sut = UnsignedLong.fromString("9999999999999999999");

        // When
        UnsignedLong result = sut.subtract(sut);

        // Then
        assertThat(result.rawBits()).isZero();
    }

    @Test
    void multiplySimple() {
        // Given
        var sut = UnsignedLong.of(1_000_000_000L);

        // When
        UnsignedLong result = sut.multiply(UnsignedLong.of(1_000_000_000L));

        // Then
        assertThat(result.rawBits()).isEqualTo(1_000_000_000_000_000_000L);
    }

    @Test
    void multiplyWrapsAroundMax() {
        // MAX * MAX mod 2^64 = 1  (same identity as unsigned short/int)
        // Given
        var sut = UnsignedLong.MAX;

        // When
        UnsignedLong result = sut.multiply(UnsignedLong.MAX);

        // Then
        assertThat(result.rawBits()).isEqualTo(1L);
    }

    @Test
    void multiplyByOne() {
        // Given
        var sut = UnsignedLong.fromString("12345678901234567890");

        // When
        UnsignedLong result = sut.multiply(UnsignedLong.of(1L));

        // Then
        assertThat(result.rawBits()).isEqualTo(sut.rawBits());
    }

    @Test
    void divideSimple() {
        // Given
        var sut = UnsignedLong.of(1_000_000_000_000L);

        // When
        UnsignedLong result = sut.divide(UnsignedLong.of(1_000_000L));

        // Then
        assertThat(result.rawBits()).isEqualTo(1_000_000L);
    }

    @Test
    void divideAboveSignedMax() {
        // 2^63 / 2 = 2^62
        // Given
        var sut = UnsignedLong.fromString("9223372036854775808");

        // When
        UnsignedLong result = sut.divide(UnsignedLong.of(2L));

        // Then
        assertThat(result.rawBits()).isEqualTo(Long.MAX_VALUE / 2 + 1);
    }

    @Test
    void divideByZeroThrows() {
        // Given
        var sut = UnsignedLong.of(1L);

        // When / Then
        assertThatThrownBy(() -> sut.divide(UnsignedLong.ZERO))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void remainderSimple() {
        // Given
        var sut = UnsignedLong.of(1_000_000_007L);

        // When
        UnsignedLong result = sut.remainder(UnsignedLong.of(3L));

        // Then
        assertThat(result.rawBits()).isEqualTo(1_000_000_007L % 3L);
    }

    @Test
    void remainderByZeroThrows() {
        // Given
        var sut = UnsignedLong.of(1L);

        // When / Then
        assertThatThrownBy(() -> sut.remainder(UnsignedLong.ZERO))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void divideRemainderRelationship() {
        // a = (a / b) * b + (a % b)  for all b != 0
        // Given
        var sut = UnsignedLong.fromString("12345678901234567890");
        var divisor = UnsignedLong.of(1_000_000_007L);

        // When
        UnsignedLong q = sut.divide(divisor);
        UnsignedLong r = sut.remainder(divisor);
        UnsignedLong result = q.multiply(divisor).add(r);

        // Then
        assertThat(result.rawBits()).isEqualTo(sut.rawBits());
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringShowsUnsignedRepresentation() {
        // Given
        var sut = UnsignedLong.MAX;

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("UnsignedLong(18446744073709551615)");
    }

    @Test
    void toStringZero() {
        // Given
        var sut = UnsignedLong.ZERO;

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("UnsignedLong(0)");
    }
}
