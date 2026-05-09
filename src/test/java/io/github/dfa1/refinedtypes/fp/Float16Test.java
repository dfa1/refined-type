package io.github.dfa1.refinedtypes.fp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Float16Test {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroRoundTrips() {
        // Given
        var sut = Float16.of(0f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void oneRoundTrips() {
        // Given
        var sut = Float16.of(1f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(1f);
    }

    @Test
    void negativeOneRoundTrips() {
        // Given
        var sut = Float16.of(-1f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(-1f);
    }

    @Test
    void rawBitsRoundTrips() {
        // 0x3C00 is the bit-pattern for 1.0 in float16
        // Given
        short input = 0x3C00;
        var sut = new Float16(input);

        // When
        short result = sut.rawBits();

        // Then
        assertThat(result).isEqualTo(input);
    }

    // ── special values ───────────────────────────────────────────────────────

    @Test
    void positiveInfinityConstant() {
        // Given
        var sut = Float16.POSITIVE_INFINITY;

        // When / Then
        assertThat(sut.isInfinite()).isTrue();
        assertThat(sut.value()).isEqualTo(Float.POSITIVE_INFINITY);
    }

    @Test
    void negativeInfinityConstant() {
        // Given
        var sut = Float16.NEGATIVE_INFINITY;

        // When / Then
        assertThat(sut.isInfinite()).isTrue();
        assertThat(sut.value()).isEqualTo(Float.NEGATIVE_INFINITY);
    }

    @Test
    void nanConstantIsNaN() {
        // Given
        var sut = Float16.NaN;

        // When / Then
        assertThat(sut.isNaN()).isTrue();
        assertThat(sut.isFinite()).isFalse();
    }

    @Test
    void maxValueIs65504() {
        // Given
        var sut = Float16.MAX_VALUE;

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(65504f);
    }

    @Test
    void isFiniteForNormalValue() {
        // Given
        var sut = Float16.of(42f);

        // When / Then
        assertThat(sut.isFinite()).isTrue();
        assertThat(sut.isNaN()).isFalse();
        assertThat(sut.isInfinite()).isFalse();
    }

    // ── arithmetic ───────────────────────────────────────────────────────────

    @Test
    void addSimple() {
        // Given
        var sut = Float16.of(1.5f);

        // When
        Float16 result = sut.add(Float16.of(0.5f));

        // Then
        assertThat(result.value()).isEqualTo(2f);
    }

    @Test
    void addIdentity() {
        // Given
        var sut = Float16.of(3.0f);

        // When
        Float16 result = sut.add(Float16.ZERO);

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
    }

    @Test
    void addOverflowYieldsInfinity() {
        // MAX + MAX overflows float16 range
        // Given
        var sut = Float16.MAX_VALUE;

        // When
        Float16 result = sut.add(Float16.MAX_VALUE);

        // Then
        assertThat(result.isInfinite()).isTrue();
    }

    @Test
    void subtractSimple() {
        // Given
        var sut = Float16.of(3.0f);

        // When
        Float16 result = sut.subtract(Float16.of(1.0f));

        // Then
        assertThat(result.value()).isEqualTo(2f);
    }

    @Test
    void subtractSelf() {
        // Given
        var sut = Float16.of(42f);

        // When
        Float16 result = sut.subtract(sut);

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void multiplySimple() {
        // Given
        var sut = Float16.of(2.0f);

        // When
        Float16 result = sut.multiply(Float16.of(3.0f));

        // Then
        assertThat(result.value()).isEqualTo(6f);
    }

    @Test
    void multiplyByOne() {
        // Given
        var sut = Float16.of(7.5f);

        // When
        Float16 result = sut.multiply(Float16.ONE);

        // Then
        assertThat(result.value()).isEqualTo(sut.value());
    }

    @Test
    void multiplyByZero() {
        // Given
        var sut = Float16.of(1234f);

        // When
        Float16 result = sut.multiply(Float16.ZERO);

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void divideSimple() {
        // Given
        var sut = Float16.of(6f);

        // When
        Float16 result = sut.divide(Float16.of(2f));

        // Then
        assertThat(result.value()).isEqualTo(3f);
    }

    @Test
    void divideByZeroYieldsInfinity() {
        // IEEE 754: x / 0 = ±Infinity
        // Given
        var sut = Float16.of(1f);

        // When
        Float16 result = sut.divide(Float16.ZERO);

        // Then
        assertThat(result.isInfinite()).isTrue();
    }

    @Test
    void nanPropagatesInArithmetic() {
        // Given
        var sut = Float16.NaN;

        // When
        Float16 result = sut.add(Float16.ONE);

        // Then
        assertThat(result.isNaN()).isTrue();
    }

    // ── negate / abs ─────────────────────────────────────────────────────────

    @Test
    void negatePositive() {
        // Given
        var sut = Float16.of(3.0f);

        // When
        Float16 result = sut.negate();

        // Then
        assertThat(result.value()).isEqualTo(-3f);
    }

    @Test
    void negateNegative() {
        // Given
        var sut = Float16.of(-3.0f);

        // When
        Float16 result = sut.negate();

        // Then
        assertThat(result.value()).isEqualTo(3f);
    }

    @Test
    void negateZeroGivesNegativeZero() {
        // IEEE 754: -0.0 and +0.0 are distinct bit patterns
        // Given
        var sut = Float16.ZERO;

        // When
        Float16 result = sut.negate();

        // Then
        assertThat(result.rawBits()).isEqualTo((short) 0x8000);
    }

    @Test
    void absOfNegative() {
        // Given
        var sut = Float16.of(-7.5f);

        // When
        Float16 result = sut.abs();

        // Then
        assertThat(result.value()).isEqualTo(7.5f);
    }

    @Test
    void absOfPositive() {
        // Given
        var sut = Float16.of(7.5f);

        // When
        Float16 result = sut.abs();

        // Then
        assertThat(result.value()).isEqualTo(7.5f);
    }

    // ── comparison ───────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = Float16.of(1f);
        var other = Float16.of(2f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqual() {
        // Given
        var sut = Float16.of(1.5f);
        var other = Float16.of(1.5f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void compareToReturnsPositiveWhenGreater() {
        // Given
        var sut = Float16.of(2f);
        var other = Float16.of(1f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void nanIsGreaterThanAllMatchingFloatCompare() {
        // Float.compare treats NaN as greater than all values, including +Infinity
        // Given
        var sut = Float16.NaN;
        var other = Float16.POSITIVE_INFINITY;

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void negativeInfinityIsLessThanEverything() {
        // Given
        var sut = Float16.NEGATIVE_INFINITY;
        var other = Float16.of(-65504f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    // ── precision ────────────────────────────────────────────────────────────

    @Test
    void valueInRange2048To4096HasUlpOfTwo() {
        // Values in [2^11, 2^12) have ULP=2 in float16: 2049 rounds to 2048
        // Given
        var sut = Float16.of(2049f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(2048f);
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringShowsFloatValue() {
        // Given
        var sut = Float16.of(1.5f);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Float16(1.5)");
    }

    @Test
    void toStringNaN() {
        // Given
        var sut = Float16.NaN;

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Float16(NaN)");
    }

    @Test
    void toStringPositiveInfinity() {
        // Given
        var sut = Float16.POSITIVE_INFINITY;

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Float16(Infinity)");
    }
}
