package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedInt;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SwissValorNumberTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void smallestValorAccepted() {
        // Given
        var sut = SwissValorNumber.MIN;

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(SwissValorNumber.MIN.value());
    }

    @Test
    void largestValorAccepted() {
        // Given
        var sut = SwissValorNumber.MAX;

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(SwissValorNumber.MAX.value());
    }

    @Test
    void typicalValorAccepted() {
        // Given
        var sut = SwissValorNumber.of(1_222_171);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(1_222_171);
    }

    @Test
    void zeroRejected() {
        // When / Then
        assertThatThrownBy(() -> SwissValorNumber.of(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> SwissValorNumber.of(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aboveMaxRejected() {
        // When / Then
        assertThatThrownBy(() -> SwissValorNumber.of(1_000_000_000))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── toIsin ──────────────────────────────────────────────────────────────

    @Test
    void abbValorProducesAbbIsin() {
        // Given
        var sut = SwissValorNumber.of(1_222_171);

        // When
        Isin result = sut.toIsin();

        // Then
        assertThat(result.value()).isEqualTo("CH0012221716");
    }

    @Test
    void nestleValorProducesNestleIsin() {
        // Given
        var sut = SwissValorNumber.of(3_886_335);

        // When
        Isin result = sut.toIsin();

        // Then
        assertThat(result.value()).isEqualTo("CH0038863350");
    }

    @Test
    void rocheValorProducesRocheIsin() {
        // Given
        var sut = SwissValorNumber.of(1_203_204);

        // When
        Isin result = sut.toIsin();

        // Then
        assertThat(result.value()).isEqualTo("CH0012032048");
    }

    @Test
    void novartisValorProducesNovartisIsin() {
        // Given
        var sut = SwissValorNumber.of(1_200_526);

        // When
        Isin result = sut.toIsin();

        // Then
        assertThat(result.value()).isEqualTo("CH0012005267");
    }

    @Test
    void shortValorIsZeroPaddedToNineDigits() {
        // Given — single-digit valor exercises the left-pad
        var sut = SwissValorNumber.of(1);

        // When
        Isin result = sut.toIsin();

        // Then
        assertThat(result.value()).startsWith("CH000000001");
        assertThat(result.value()).hasSize(12);
    }

    @Test
    void toIsinExposesCountryCodePrefix() {
        // Given
        var sut = SwissValorNumber.of(1_222_171);

        // When
        CountryCode result = sut.toIsin().country();

        // Then
        assertThat(result.value()).isEqualTo("CH");
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = SwissValorNumber.of(100);
        var other = SwissValorNumber.of(200);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = SwissValorNumber.of(1_222_171);
        var other = SwissValorNumber.of(1_222_171);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = SwissValorNumber.of(1_222_171);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("SwissValorNumber(1222171)");
    }

    // ── RefinedInt ──────────────────────────────────────────────────────────

    @Test
    void implementsRefinedInt() {
        // Given
        RefinedInt<SwissValorNumber> sut = SwissValorNumber.of(42_000);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(42_000);
    }
}
