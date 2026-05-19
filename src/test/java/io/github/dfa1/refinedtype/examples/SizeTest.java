package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedLong;
import io.github.dfa1.refinedtype.examples.Size.Unit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SizeTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = Size.of(0L);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void positiveBytesAccepted() {
        // Given
        var sut = Size.of(1234L);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isEqualTo(1234L);
    }

    @Test
    void maxLongAccepted() {
        // Given
        var sut = Size.of(Long.MAX_VALUE);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> Size.of(-1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── factory ─────────────────────────────────────────────────────────────

    @Test
    void ofBytesEqualsConstructor() {
        // Given / When
        Size result = Size.of(42L, Unit.B);

        // Then
        assertThat(result.value()).isEqualTo(42L);
    }

    @Test
    void ofKilobytesMultipliesByBinaryK() {
        // Given / When
        Size result = Size.of(2L, Unit.KB);

        // Then
        assertThat(result.value()).isEqualTo(2L * 1024);
    }

    @Test
    void ofMegabytesMultipliesByBinaryM() {
        // Given / When
        Size result = Size.of(3L, Unit.MB);

        // Then
        assertThat(result.value()).isEqualTo(3L * 1024 * 1024);
    }

    @Test
    void ofGigabytesMultipliesByBinaryG() {
        // Given / When
        Size result = Size.of(4L, Unit.GB);

        // Then
        assertThat(result.value()).isEqualTo(4L * 1024 * 1024 * 1024);
    }

    @Test
    void ofTerabytesMultipliesByBinaryT() {
        // Given / When
        Size result = Size.of(1L, Unit.TB);

        // Then
        assertThat(result.value()).isEqualTo(1024L * 1024 * 1024 * 1024);
    }

    @Test
    void ofGigabytesOverflowDetected() {
        // Long.MAX_VALUE / GB ≈ 8 589 934 591 — anything above overflows
        // When / Then
        assertThatThrownBy(() -> Size.of(Long.MAX_VALUE, Unit.GB))
                .isInstanceOf(ArithmeticException.class);
    }

    // ── unit accessor ───────────────────────────────────────────────────────

    @Test
    void toKilobytesTruncates() {
        // Given
        var sut = Size.of(2_500L, Unit.B); // 2 KiB + 452 B

        // When
        long result = sut.to(Unit.KB);

        // Then
        assertThat(result).isEqualTo(2L);
    }

    @Test
    void toMegabytesExact() {
        // Given
        var sut = Size.of(7L, Unit.MB);

        // When
        long result = sut.to(Unit.MB);

        // Then
        assertThat(result).isEqualTo(7L);
    }

    @Test
    void toGigabytesAboveIntCeiling() {
        // 5 GiB > Integer.MAX_VALUE — confirms long backing matters
        // Given
        var sut = Size.of(5L, Unit.GB);

        // When
        long result = sut.to(Unit.GB);

        // Then
        assertThat(result).isEqualTo(5L);
        assertThat(sut.to(Unit.B)).isGreaterThan((long) Integer.MAX_VALUE);
    }

    // ── unit enum ───────────────────────────────────────────────────────────

    @Test
    void unitBytesMatchBinaryMultipliers() {
        // When / Then
        assertThat(Unit.B.bytes).isEqualTo(1L);
        assertThat(Unit.KB.bytes).isEqualTo(1024L);
        assertThat(Unit.MB.bytes).isEqualTo(1024L * 1024L);
        assertThat(Unit.GB.bytes).isEqualTo(1024L * 1024L * 1024L);
        assertThat(Unit.TB.bytes).isEqualTo(1024L * 1024L * 1024L * 1024L);
    }

    // ── arithmetic ──────────────────────────────────────────────────────────

    @Test
    void plusAddsBytes() {
        // Given
        var sut = Size.of(1, Unit.KB);

        // When
        Size result = sut.plus(Size.of(3, Unit.KB));

        // Then
        assertThat(result.value()).isEqualTo(4L * 1024);
    }

    @Test
    void plusOverflowDetected() {
        // Given
        var sut = Size.of(Long.MAX_VALUE);

        // When / Then
        assertThatThrownBy(() -> sut.plus(Size.of(1L)))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void minusSubtractsBytes() {
        // Given
        var sut = Size.of(5, Unit.KB);

        // When
        Size result = sut.minus(Size.of(2, Unit.KB));

        // Then
        assertThat(result.value()).isEqualTo(3L * 1024);
    }

    @Test
    void minusGoingNegativeRejected() {
        // Given
        var sut = Size.of(10, Unit.B);

        // When / Then
        assertThatThrownBy(() -> sut.minus(Size.of(11, Unit.B)))
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
        var sut = Size.of(1, Unit.B);

        // When
        boolean result = sut.isZero();

        // Then
        assertThat(result).isFalse();
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = Size.of(1, Unit.KB);
        var other = Size.of(1, Unit.MB);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsPositiveWhenLarger() {
        // Given
        var sut = Size.of(1, Unit.GB);
        var other = Size.of(1, Unit.MB);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsZeroForEqual() {
        // Given
        var sut = Size.of(2, Unit.KB);
        var other = Size.of(2 * 1024, Unit.B);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringBytesBelowKi() {
        // Given
        var sut = Size.of(512, Unit.B);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Size(512 B)");
    }

    @Test
    void toStringKibibytes() {
        // Given
        var sut = Size.of(1536, Unit.B); // 1.5 KiB

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Size(1.5 KiB)");
    }

    @Test
    void toStringMebibytes() {
        // Given
        var sut = Size.of(3, Unit.MB);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Size(3.0 MiB)");
    }

    @Test
    void toStringGibibytes() {
        // Given
        var sut = Size.of(4, Unit.GB);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Size(4.0 GiB)");
    }

    @Test
    void toStringTebibytes() {
        // Given
        var sut = Size.of(2, Unit.TB);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Size(2.0 TiB)");
    }

    // ── RefinedLong ─────────────────────────────────────────────────────────

    @Test
    void implementsRefinedLong() {
        // Given
        RefinedLong<Size> sut = Size.of(8, Unit.MB);

        // When
        long result = sut.value();

        // Then
        assertThat(result).isEqualTo(8L * 1024 * 1024);
    }
}
