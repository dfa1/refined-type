package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyCodeTest {

    @Test
    void valueReturnsUppercaseCode() {
        // Given
        var sut = new CurrencyCode("EUR");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("EUR");
    }

    @Test
    void lowercaseNormalizedToUppercase() {
        // Given
        var sut = new CurrencyCode("usd");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("USD");
    }

    @Test
    void mixedCaseNormalized() {
        // Given
        var sut = new CurrencyCode("gBp");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("GBP");
    }

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> new CurrencyCode(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooShortRejected() {
        // Given
        String input = "EU";

        // When / Then
        assertThatThrownBy(() -> new CurrencyCode(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooLongRejected() {
        // Given
        String input = "EURO";

        // When / Then
        assertThatThrownBy(() -> new CurrencyCode(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nonLettersRejected() {
        // When / Then
        assertThatThrownBy(() -> new CurrencyCode("US1"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CurrencyCode("1SD"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = new CurrencyCode("EUR");
        var other = new CurrencyCode("USD");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsPositiveWhenLexicographicallyGreater() {
        // Given
        var sut = new CurrencyCode("USD");
        var other = new CurrencyCode("EUR");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsZeroRegardlessOfInputCase() {
        // Given
        var sut = new CurrencyCode("EUR");
        var other = new CurrencyCode("eur");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void toStringFormat() {
        // Given
        var sut = new CurrencyCode("CHF");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("CurrencyCode(CHF)");
    }

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = new CurrencyCode("JPY");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("JPY");
    }
}
