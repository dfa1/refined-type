package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyCodeTest {

    @Test
    void valueReturnsUppercaseCode() {
        // Given
        var sut = CurrencyCode.of("EUR");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("EUR");
    }

    @Test
    void lowercaseNormalizedToUppercase() {
        // Given
        var sut = CurrencyCode.of("usd");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("USD");
    }

    @Test
    void mixedCaseNormalized() {
        // Given
        var sut = CurrencyCode.of("gBp");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("GBP");
    }

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> CurrencyCode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooShortRejected() {
        // Given
        String input = "EU";

        // When / Then
        assertThatThrownBy(() -> CurrencyCode.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooLongRejected() {
        // Given
        String input = "EURO";

        // When / Then
        assertThatThrownBy(() -> CurrencyCode.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nonLettersRejected() {
        // When / Then
        assertThatThrownBy(() -> CurrencyCode.of("US1"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CurrencyCode.of("1SD"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = CurrencyCode.of("EUR");
        var other = CurrencyCode.of("USD");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsPositiveWhenLexicographicallyGreater() {
        // Given
        var sut = CurrencyCode.of("USD");
        var other = CurrencyCode.of("EUR");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsZeroRegardlessOfInputCase() {
        // Given
        var sut = CurrencyCode.of("EUR");
        var other = CurrencyCode.of("eur");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void toStringFormat() {
        // Given
        var sut = CurrencyCode.of("CHF");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("CurrencyCode(CHF)");
    }

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = CurrencyCode.of("JPY");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("JPY");
    }
}
