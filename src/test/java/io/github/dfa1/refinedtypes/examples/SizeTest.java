package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedLong;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SizeTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = new Size(0L);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void positiveBytesAccepted() {
        // Given
        var sut = new Size(1234L);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isEqualTo(1234L);
    }

    @Test
    void maxLongAccepted() {
        // Given
        var sut = new Size(Long.MAX_VALUE);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> new Size(-1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── factories ───────────────────────────────────────────────────────────

    @Test
    void ofBytesEqualsConstructor() {
        // Given / When
        Size result = Size.ofBytes(42L);

        // Then
        assertThat(result.value()).isEqualTo(42L);
    }

    @Test
    void ofKilobytesMultipliesByBinaryK() {
        // Given / When
        Size result = Size.ofKilobytes(2L);

        // Then
        assertThat(result.value()).isEqualTo(2L * 1024);
    }

    @Test
    void ofMegabytesMultipliesByBinaryM() {
        // Given / When
        Size result = Size.ofMegabytes(3L);

        // Then
        assertThat(result.value()).isEqualTo(3L * 1024 * 1024);
    }

    @Test
    void ofGigabytesMultipliesByBinaryG() {
        // Given / When
        Size result = Size.ofGigabytes(4L);

        // Then
        assertThat(result.value()).isEqualTo(4L * 1024 * 1024 * 1024);
    }

    @Test
    void ofTerabytesMultipliesByBinaryT() {
        // Given / When
        Size result = Size.ofTerabytes(1L);

        // Then
        assertThat(result.value()).isEqualTo(1024L * 1024 * 1024 * 1024);
    }

    @Test
    void ofGigabytesOverflowDetected() {
        // Long.MAX_VALUE / GB ≈ 8 589 934 591 — anything above overflows
        // When / Then
        assertThatThrownBy(() -> Size.ofGigabytes(Long.MAX_VALUE))
                .isInstanceOf(ArithmeticException.class);
    }

    // ── unit accessors ──────────────────────────────────────────────────────

    @Test
    void toKilobytesTruncates() {
        // Given
        var sut = Size.ofBytes(2_500L); // 2 KiB + 452 B

        // When
        long result = sut.toKilobytes();

        // Then
        assertThat(result).isEqualTo(2L);
    }

    @Test
    void toMegabytesExact() {
        // Given
        var sut = Size.ofMegabytes(7L);

        // When
        long result = sut.toMegabytes();

        // Then
        assertThat(result).isEqualTo(7L);
    }

    @Test
    void toGigabytesAboveIntCeiling() {
        // 5 GiB > Integer.MAX_VALUE — confirms long backing matters
        // Given
        var sut = Size.ofGigabytes(5L);

        // When
        long result = sut.toGigabytes();

        // Then
        assertThat(result).isEqualTo(5L);
        assertThat(sut.toBytes()).isGreaterThan((long) Integer.MAX_VALUE);
    }

    // ── arithmetic ──────────────────────────────────────────────────────────

    @Test
    void plusAddsBytes() {
        // Given
        var sut = Size.ofKilobytes(1);

        // When
        Size result = sut.plus(Size.ofKilobytes(3));

        // Then
        assertThat(result.value()).isEqualTo(4L * 1024);
    }

    @Test
    void plusOverflowDetected() {
        // Given
        var sut = new Size(Long.MAX_VALUE);

        // When / Then
        assertThatThrownBy(() -> sut.plus(new Size(1L)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void minusSubtractsBytes() {
        // Given
        var sut = Size.ofKilobytes(5);

        // When
        Size result = sut.minus(Size.ofKilobytes(2));

        // Then
        assertThat(result.value()).isEqualTo(3L * 1024);
    }

    @Test
    void minusGoingNegativeRejected() {
        // Given
        var sut = Size.ofBytes(10);

        // When / Then
        assertThatThrownBy(() -> sut.minus(Size.ofBytes(11)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isZeroTrueForZero() {
        // Given
        var sut = Size.ZERO;

        // When
        boolean result = sut.isZero();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isZeroFalseForPositive() {
        // Given
        var sut = Size.ofBytes(1);

        // When
        boolean result = sut.isZero();

        // Then
        assertThat(result).isFalse();
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = Size.ofKilobytes(1);
        var other = Size.ofMegabytes(1);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsPositiveWhenLarger() {
        // Given
        var sut = Size.ofGigabytes(1);
        var other = Size.ofMegabytes(1);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsZeroForEqual() {
        // Given
        var sut = Size.ofKilobytes(2);
        var other = Size.ofBytes(2 * 1024);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringBytesBelowKi() {
        // Given
        var sut = Size.ofBytes(512);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Size(512 B)");
    }

    @Test
    void toStringKibibytes() {
        // Given
        var sut = Size.ofBytes(1536); // 1.5 KiB

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Size(1.5 KiB)");
    }

    @Test
    void toStringMebibytes() {
        // Given
        var sut = Size.ofMegabytes(3);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Size(3.0 MiB)");
    }

    @Test
    void toStringGibibytes() {
        // Given
        var sut = Size.ofGigabytes(4);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Size(4.0 GiB)");
    }

    @Test
    void toStringTebibytes() {
        // Given
        var sut = Size.ofTerabytes(2);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Size(2.0 TiB)");
    }

    // ── RefinedLong ─────────────────────────────────────────────────────────

    @Test
    void implementsRefinedLong() {
        // Given
        RefinedLong sut = Size.ofMegabytes(8);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isEqualTo(8L * 1024 * 1024);
    }
}
