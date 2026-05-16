package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CountryCodeTest {

    @Test
    void valueReturnsUppercaseCode() {
        // Given
        var sut = new CountryCode("DE");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("DE");
    }

    @Test
    void lowercaseNormalizedToUppercase() {
        // Given
        var sut = new CountryCode("it");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("IT");
    }

    @Test
    void mixedCaseNormalized() {
        // Given
        var sut = new CountryCode("gB");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("GB");
    }

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> new CountryCode(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooShortRejected() {
        // Given
        String input = "D";

        // When / Then
        assertThatThrownBy(() -> new CountryCode(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooLongRejected() {
        // Given
        String input = "DEU";

        // When / Then
        assertThatThrownBy(() -> new CountryCode(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nonLettersRejected() {
        // When / Then
        assertThatThrownBy(() -> new CountryCode("1T"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CountryCode("D2"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = new CountryCode("DE");
        var other = new CountryCode("US");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsPositiveWhenLexicographicallyGreater() {
        // Given
        var sut = new CountryCode("US");
        var other = new CountryCode("DE");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsZeroRegardlessOfInputCase() {
        // Given
        var sut = new CountryCode("DE");
        var other = new CountryCode("de");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void toStringFormat() {
        // Given
        var sut = new CountryCode("FR");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("CountryCode(FR)");
    }

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = new CountryCode("JP");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("JP");
    }
}
