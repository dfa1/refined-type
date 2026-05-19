package io.github.dfa1.refinedtype.jpa;

import io.github.dfa1.refinedtype.examples.CountryCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CountryCodeConverterTest {

    @Test
    void convertToDatabaseColumnReturnsCountryCode() {
        // Given
        var sut = new CountryCodeConverter();
        var countryCode = CountryCode.of("DE");

        // When
        String result = sut.convertToDatabaseColumn(countryCode);

        // Then
        assertThat(result).isEqualTo("DE");
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNull() {
        // Given
        var sut = new CountryCodeConverter();

        // When
        String result = sut.convertToDatabaseColumn(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeReturnsCountryCode() {
        // Given
        var sut = new CountryCodeConverter();

        // When
        CountryCode result = sut.convertToEntityAttribute("FR");

        // Then
        assertThat(result.value()).isEqualTo("FR");
    }

    @Test
    void convertToEntityAttributeReturnsNullForNull() {
        // Given
        var sut = new CountryCodeConverter();

        // When
        CountryCode result = sut.convertToEntityAttribute(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeNormalizesLowercase() {
        // Given
        var sut = new CountryCodeConverter();

        // When
        CountryCode result = sut.convertToEntityAttribute("de");

        // Then
        assertThat(result.value()).isEqualTo("DE");
    }

    @Test
    void convertToEntityAttributeRejectsInvalidData() {
        // Given
        var sut = new CountryCodeConverter();

        // When / Then
        assertThatThrownBy(() -> sut.convertToEntityAttribute("USA"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void roundtripPreservesValue() {
        // Given
        var sut = new CountryCodeConverter();
        var countryCode = CountryCode.of("IT");

        // When
        CountryCode result = sut.convertToEntityAttribute(sut.convertToDatabaseColumn(countryCode));

        // Then
        assertThat(result.value()).isEqualTo(countryCode.value());
    }
}
