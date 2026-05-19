package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CountryCodeTest {

    @Test
    void valueReturnsUppercaseCode() {
        // Given
        var sut = CountryCode.of("DE");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("DE");
    }

    @Test
    void lowercaseNormalizedToUppercase() {
        // Given
        var sut = CountryCode.of("it");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("IT");
    }

    @Test
    void mixedCaseNormalized() {
        // Given
        var sut = CountryCode.of("gB");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("GB");
    }

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> CountryCode.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooShortRejected() {
        // Given
        String input = "D";

        // When / Then
        assertThatThrownBy(() -> CountryCode.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooLongRejected() {
        // Given
        String input = "DEU";

        // When / Then
        assertThatThrownBy(() -> CountryCode.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nonLettersRejected() {
        // When / Then
        assertThatThrownBy(() -> CountryCode.of("1T"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> CountryCode.of("D2"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = CountryCode.of("DE");
        var other = CountryCode.of("US");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsPositiveWhenLexicographicallyGreater() {
        // Given
        var sut = CountryCode.of("US");
        var other = CountryCode.of("DE");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsZeroRegardlessOfInputCase() {
        // Given
        var sut = CountryCode.of("DE");
        var other = CountryCode.of("de");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void toStringFormat() {
        // Given
        var sut = CountryCode.of("FR");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("CountryCode(FR)");
    }

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = CountryCode.of("JP");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("JP");
    }
}
