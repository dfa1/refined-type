package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CusipNumberTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void allDigitCusipAccepted() {
        // Given
        var sut = new CusipNumber("037833100");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("037833100");
    }

    @Test
    void alphanumericCusipAccepted() {
        // Given — Alphabet Class A contains the letter K
        var sut = new CusipNumber("02079K305");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("02079K305");
    }

    @Test
    void lowercaseInputUppercased() {
        // Given
        var sut = new CusipNumber("02079k305");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("02079K305");
    }

    @Test
    void shortInputRejected() {
        // When / Then
        assertThatThrownBy(() -> new CusipNumber("12345678"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void longInputRejected() {
        // When / Then
        assertThatThrownBy(() -> new CusipNumber("1234567890"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void invalidCharRejected() {
        // When / Then
        assertThatThrownBy(() -> new CusipNumber("12345-789"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> new CusipNumber(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── toIsin ──────────────────────────────────────────────────────────────

    @Test
    void appleCusipProducesAppleIsin() {
        // Given
        var sut = new CusipNumber("037833100");

        // When
        Isin result = sut.toIsin();

        // Then
        assertThat(result.value()).isEqualTo("US0378331005");
    }

    @Test
    void microsoftCusipProducesMicrosoftIsin() {
        // Given
        var sut = new CusipNumber("594918104");

        // When
        Isin result = sut.toIsin();

        // Then
        assertThat(result.value()).isEqualTo("US5949181045");
    }

    @Test
    void alphabetCusipWithLetterProducesAlphabetIsin() {
        // Given
        var sut = new CusipNumber("02079K305");

        // When
        Isin result = sut.toIsin();

        // Then
        assertThat(result.value()).isEqualTo("US02079K3059");
    }

    @Test
    void ibmCusipProducesIbmIsin() {
        // Given
        var sut = new CusipNumber("459200101");

        // When
        Isin result = sut.toIsin();

        // Then
        assertThat(result.value()).isEqualTo("US4592001014");
    }

    @Test
    void teslaCusipWithLetterProducesTeslaIsin() {
        // Given
        var sut = new CusipNumber("88160R101");

        // When
        Isin result = sut.toIsin();

        // Then
        assertThat(result.value()).isEqualTo("US88160R1014");
    }

    @Test
    void toIsinExposesCountryPrefix() {
        // Given
        var sut = new CusipNumber("037833100");

        // When
        Country result = sut.toIsin().country();

        // Then
        assertThat(result.value()).isEqualTo("US");
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmallerLexicographically() {
        // Given
        var sut = new CusipNumber("037833100");
        var other = new CusipNumber("594918104");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new CusipNumber("037833100");
        var other = new CusipNumber("037833100");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = new CusipNumber("037833100");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("CusipNumber(037833100)");
    }

    // ── RefinedString ───────────────────────────────────────────────────────

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString<CusipNumber> sut = new CusipNumber("037833100");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("037833100");
    }
}
