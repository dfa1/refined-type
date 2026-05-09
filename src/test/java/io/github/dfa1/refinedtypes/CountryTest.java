package io.github.dfa1.refinedtypes;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CountryTest {

    @Test
    void valueReturnsUppercaseCode() {
        // Given
        var sut = new Country("DE");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("DE");
    }

    @Test
    void lowercaseNormalizedToUppercase() {
        // Given
        var sut = new Country("it");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("IT");
    }

    @Test
    void mixedCaseNormalized() {
        // Given
        var sut = new Country("gB");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("GB");
    }

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> new Country(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooShortRejected() {
        // Given
        String input = "D";

        // When / Then
        assertThatThrownBy(() -> new Country(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooLongRejected() {
        // Given
        String input = "DEU";

        // When / Then
        assertThatThrownBy(() -> new Country(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nonLettersRejected() {
        // When / Then
        assertThatThrownBy(() -> new Country("1T"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Country("D2"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = new Country("DE");
        var other = new Country("US");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsPositiveWhenLexicographicallyGreater() {
        // Given
        var sut = new Country("US");
        var other = new Country("DE");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsZeroRegardlessOfInputCase() {
        // Given
        var sut = new Country("DE");
        var other = new Country("de");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void toStringFormat() {
        // Given
        var sut = new Country("FR");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Country(FR)");
    }

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = new Country("JP");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("JP");
    }
}
