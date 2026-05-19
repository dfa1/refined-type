package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IsinTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void valueReturnsNormalizedCode() {
        // Given
        var sut = Isin.of("US0378331005");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("US0378331005");
    }

    @Test
    void lowercaseNormalizedToUppercase() {
        // Given
        var sut = Isin.of("us0378331005");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("US0378331005");
    }

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> Isin.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooShortRejected() {
        // Given
        String input = "US037833100"; // 11 chars

        // When / Then
        assertThatThrownBy(() -> Isin.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooLongRejected() {
        // Given
        String input = "US03783310055"; // 13 chars

        // When / Then
        assertThatThrownBy(() -> Isin.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void invalidCountryCodePrefixRejected() {
        // digits in country prefix violate ^[A-Z]{2}
        // Given
        String input = "1S0378331005";

        // When / Then
        assertThatThrownBy(() -> Isin.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void invalidCheckDigitRejected() {
        // last char must be digit
        // Given
        String input = "US037833100A";

        // When / Then
        assertThatThrownBy(() -> Isin.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void specialCharsRejected() {
        // Given
        String input = "US03783310-5";

        // When / Then
        assertThatThrownBy(() -> Isin.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── (CountryCode, NSIN) constructor ─────────────────────────────────────────

    @Test
    void countryNsinConstructorComputesCheckDigit() {
        // Given — Apple Inc., CUSIP 037833100, US prefix
        var country = CountryCode.of("US");

        // When
        Isin result = Isin.of(country, "037833100");

        // Then
        assertThat(result.value()).isEqualTo("US0378331005");
    }

    @Test
    void countryNsinConstructorAcceptsLowercaseNsin() {
        // Given — Alphabet Class A, embedded 'k'
        var country = CountryCode.of("US");

        // When
        Isin result = Isin.of(country, "02079k305");

        // Then
        assertThat(result.value()).isEqualTo("US02079K3059");
    }

    @Test
    void countryNsinConstructorRejectsShortNsin() {
        // Given
        var country = CountryCode.of("US");

        // When / Then
        assertThatThrownBy(() -> Isin.of(country, "12345678"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void countryNsinConstructorRejectsInvalidChar() {
        // Given
        var country = CountryCode.of("US");

        // When / Then
        assertThatThrownBy(() -> Isin.of(country, "12345-789"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void countryNsinConstructorRejectsNull() {
        // Given
        var country = CountryCode.of("US");

        // When / Then
        assertThatThrownBy(() -> Isin.of(country, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── country() ───────────────────────────────────────────────────────────

    @Test
    void countryExtractedFromPrefix() {
        // Given
        var sut = Isin.of("US0378331005");

        // When
        CountryCode result = sut.country();

        // Then
        assertThat(result.value()).isEqualTo("US");
    }

    @Test
    void countryWorksForDifferentPrefixes() {
        // Given
        var sut = Isin.of("DE0005140008"); // Deutsche Bank

        // When
        CountryCode result = sut.country();

        // Then
        assertThat(result.value()).isEqualTo("DE");
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = Isin.of("DE0005140008");
        var other = Isin.of("US0378331005");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualCodes() {
        // Given
        var sut = Isin.of("US0378331005");
        var other = Isin.of("us0378331005"); // lowercase normalized

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringFormat() {
        // Given
        var sut = Isin.of("US0378331005");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Isin(US0378331005)");
    }

    // ── RefinedString ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = Isin.of("US0378331005");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("US0378331005");
    }
}
