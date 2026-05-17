package io.github.dfa1.refinedtype.unsigned;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnsignedByteTest {

    // ── fromString ───────────────────────────────────────────────────────────

    @Test
    void fromStringZero() {
        // Given / When
        UnsignedByte result = UnsignedByte.fromString("0");

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void fromStringMaxValue() {
        // Given / When
        UnsignedByte result = UnsignedByte.fromString("255");

        // Then
        assertThat(result.value()).isEqualTo(255);
    }

    @Test
    void fromStringAboveMaxThrows() {
        // Given / When / Then
        assertThatThrownBy(() -> UnsignedByte.fromString("256"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = new UnsignedByte(0);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxValueIsValid() {
        // Given
        var sut = new UnsignedByte(UnsignedByte.MAX_VALUE);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(255);
    }

    @Test
    void negativeRejected() {
        // Given
        int input = -1;

        // When / Then
        assertThatThrownBy(() -> new UnsignedByte(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aboveMaxRejected() {
        // Given
        int input = 256;

        // When / Then
        assertThatThrownBy(() -> new UnsignedByte(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── unsigned bit-pattern semantics ──────────────────────────────────────

    @Test
    void valueAboveSignedMaxRecoveredCorrectly() {
        // 2^7 = 128 — raw bits are Byte.MIN_VALUE (negative signed),
        // but Byte.toUnsignedInt must recover the correct unsigned value.
        // Given
        var sut = new UnsignedByte(128);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(128);
        assertThat(sut.rawBits()).isEqualTo(Byte.MIN_VALUE);
    }

    @Test
    void valuePreservesAll8Bits() {
        // 0xBE = 190
        // Given
        var sut = new UnsignedByte(0xBE);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(190);
        assertThat(Byte.toUnsignedInt(sut.rawBits())).isEqualTo(190);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsPositiveWhenUnsignedGreater() {
        // 128 > 1 in unsigned space even though raw bits are negative
        // Given
        var sut = new UnsignedByte(128);
        var other = new UnsignedByte(1);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsNegativeWhenUnsignedSmaller() {
        // Given
        var sut = new UnsignedByte(1);
        var other = new UnsignedByte(128);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new UnsignedByte(200);
        var other = new UnsignedByte(200);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxIsGreaterThanSignedMax() {
        // Given
        var sut = new UnsignedByte(UnsignedByte.MAX_VALUE);
        var signedMax = new UnsignedByte(Byte.MAX_VALUE);

        // When
        int result = sut.compareTo(signedMax);

        // Then
        assertThat(result).isPositive();
    }

    // ── arithmetic ───────────────────────────────────────────────────────────

    @Test
    void addSimple() {
        // Given
        var sut = new UnsignedByte(100);

        // When
        UnsignedByte result = sut.add(new UnsignedByte(50));

        // Then
        assertThat(result.value()).isEqualTo(150);
    }

    @Test
    void addWrapsAroundMax() {
        // MAX + 1 must wrap to 0
        // Given
        var sut = new UnsignedByte(UnsignedByte.MAX_VALUE);

        // When
        UnsignedByte result = sut.add(new UnsignedByte(1));

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void addIdentity() {
        // Given
        var sut = new UnsignedByte(0xBE);

        // When
        UnsignedByte result = sut.add(new UnsignedByte(0));

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
    }

    @Test
    void subtractSimple() {
        // Given
        var sut = new UnsignedByte(10);

        // When
        UnsignedByte result = sut.subtract(new UnsignedByte(3));

        // Then
        assertThat(result.value()).isEqualTo(7);
    }

    @Test
    void subtractWrapsAroundZero() {
        // 0 - 1 must wrap to MAX
        // Given
        var sut = new UnsignedByte(0);

        // When
        UnsignedByte result = sut.subtract(new UnsignedByte(1));

        // Then
        assertThat(result.value()).isEqualTo(UnsignedByte.MAX_VALUE);
    }

    @Test
    void subtractSelf() {
        // Given
        var sut = new UnsignedByte(200);

        // When
        UnsignedByte result = sut.subtract(sut);

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void multiplySimple() {
        // Given
        var sut = new UnsignedByte(12);

        // When
        UnsignedByte result = sut.multiply(new UnsignedByte(10));

        // Then
        assertThat(result.value()).isEqualTo(120);
    }

    @Test
    void multiplyWrapsAroundMax() {
        // MAX * MAX mod 2^8 = (2^8-1)^2 mod 2^8 = 1
        // Given
        var sut = new UnsignedByte(UnsignedByte.MAX_VALUE);

        // When
        UnsignedByte result = sut.multiply(new UnsignedByte(UnsignedByte.MAX_VALUE));

        // Then
        assertThat(result.value()).isEqualTo(1);
    }

    @Test
    void multiplyByZero() {
        // Given
        var sut = new UnsignedByte(0xBE);

        // When
        UnsignedByte result = sut.multiply(new UnsignedByte(0));

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void multiplyByOne() {
        // Given
        var sut = new UnsignedByte(0xBE);

        // When
        UnsignedByte result = sut.multiply(new UnsignedByte(1));

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
    }

    @Test
    void divideSimple() {
        // Given
        var sut = new UnsignedByte(200);

        // When
        UnsignedByte result = sut.divide(new UnsignedByte(4));

        // Then
        assertThat(result.value()).isEqualTo(50);
    }

    @Test
    void divideAboveSignedMax() {
        // 200 / 2 = 100 — value above Byte.MAX_VALUE
        // Given
        var sut = new UnsignedByte(200);

        // When
        UnsignedByte result = sut.divide(new UnsignedByte(2));

        // Then
        assertThat(result.value()).isEqualTo(100);
    }

    @Test
    void divideByZeroThrows() {
        // Given
        var sut = new UnsignedByte(1);

        // When / Then
        assertThatThrownBy(() -> sut.divide(new UnsignedByte(0)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void remainderSimple() {
        // Given
        var sut = new UnsignedByte(200);

        // When
        UnsignedByte result = sut.remainder(new UnsignedByte(7));

        // Then
        assertThat(result.value()).isEqualTo(200 % 7);
    }

    @Test
    void remainderByZeroThrows() {
        // Given
        var sut = new UnsignedByte(1);

        // When / Then
        assertThatThrownBy(() -> sut.remainder(new UnsignedByte(0)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void divideRemainderRelationship() {
        // a = (a / b) * b + (a % b)  for all b != 0
        // Given
        var sut = new UnsignedByte(200);
        var divisor = new UnsignedByte(7);

        // When
        UnsignedByte q = sut.divide(divisor);
        UnsignedByte r = sut.remainder(divisor);
        UnsignedByte result = q.multiply(divisor).add(r);

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
    }

    // ── widening ─────────────────────────────────────────────────────────────

    @Test
    void toUnsignedShortPreservesValue() {
        // Given
        var sut = new UnsignedByte(UnsignedByte.MAX_VALUE);

        // When
        UnsignedShort result = sut.toUnsignedShort();

        // Then
        assertThat(result.value()).isEqualTo(255);
    }

    @Test
    void toUnsignedIntPreservesValue() {
        // Given
        var sut = new UnsignedByte(UnsignedByte.MAX_VALUE);

        // When
        UnsignedInt result = sut.toUnsignedInt();

        // Then
        assertThat(result.value()).isEqualTo(255L);
    }

    @Test
    void toUnsignedLongPreservesValue() {
        // Given
        var sut = new UnsignedByte(UnsignedByte.MAX_VALUE);

        // When
        UnsignedLong result = sut.toUnsignedLong();

        // Then
        assertThat(Long.toUnsignedString(result.rawBits())).isEqualTo("255");
    }

    @Test
    void wideningEnablesCrossTypeArithmetic() {
        // UnsignedByte + UnsignedShort — widen first, then add
        // Given
        var sut = new UnsignedByte(200);
        var other = new UnsignedShort(1_000);

        // When
        UnsignedShort result = sut.toUnsignedShort().add(other);

        // Then
        assertThat(result.value()).isEqualTo(1_200);
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringShowsUnsignedRepresentation() {
        // Given
        var sut = new UnsignedByte(UnsignedByte.MAX_VALUE);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("UnsignedByte(255)");
    }

    @Test
    void toStringZero() {
        // Given
        var sut = new UnsignedByte(0);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("UnsignedByte(0)");
    }
}
