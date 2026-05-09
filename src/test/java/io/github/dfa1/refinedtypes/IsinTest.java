package io.github.dfa1.refinedtypes;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IsinTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void valueReturnsNormalizedCode() {
        // Given
        var sut = new Isin("US0378331005");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("US0378331005");
    }

    @Test
    void lowercaseNormalizedToUppercase() {
        // Given
        var sut = new Isin("us0378331005");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("US0378331005");
    }

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> new Isin(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooShortRejected() {
        // Given
        String input = "US037833100"; // 11 chars

        // When / Then
        assertThatThrownBy(() -> new Isin(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooLongRejected() {
        // Given
        String input = "US03783310055"; // 13 chars

        // When / Then
        assertThatThrownBy(() -> new Isin(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void invalidCountryPrefixRejected() {
        // digits in country prefix violate ^[A-Z]{2}
        // Given
        String input = "1S0378331005";

        // When / Then
        assertThatThrownBy(() -> new Isin(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void invalidCheckDigitRejected() {
        // last char must be digit
        // Given
        String input = "US037833100A";

        // When / Then
        assertThatThrownBy(() -> new Isin(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void specialCharsRejected() {
        // Given
        String input = "US03783310-5";

        // When / Then
        assertThatThrownBy(() -> new Isin(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── country() ───────────────────────────────────────────────────────────

    @Test
    void countryExtractedFromPrefix() {
        // Given
        var sut = new Isin("US0378331005");

        // When
        Country result = sut.country();

        // Then
        assertThat(result.value()).isEqualTo("US");
    }

    @Test
    void countryWorksForDifferentPrefixes() {
        // Given
        var sut = new Isin("DE0005140008"); // Deutsche Bank

        // When
        Country result = sut.country();

        // Then
        assertThat(result.value()).isEqualTo("DE");
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = new Isin("DE0005140008");
        var other = new Isin("US0378331005");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualCodes() {
        // Given
        var sut = new Isin("US0378331005");
        var other = new Isin("us0378331005"); // lowercase normalized

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringFormat() {
        // Given
        var sut = new Isin("US0378331005");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Isin(US0378331005)");
    }

    // ── RefinedString ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = new Isin("US0378331005");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("US0378331005");
    }
}
