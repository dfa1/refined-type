package io.github.dfa1.refinedtype.jpa;

import io.github.dfa1.refinedtype.examples.CurrencyCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyCodeConverterTest {

    @Test
    void convertToDatabaseColumnReturnsCurrencyCode() {
        // Given
        var sut = new CurrencyCodeConverter();
        var currencyCode = new CurrencyCode("EUR");

        // When
        String result = sut.convertToDatabaseColumn(currencyCode);

        // Then
        assertThat(result).isEqualTo("EUR");
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNull() {
        // Given
        var sut = new CurrencyCodeConverter();

        // When
        String result = sut.convertToDatabaseColumn(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeReturnsCurrencyCode() {
        // Given
        var sut = new CurrencyCodeConverter();

        // When
        CurrencyCode result = sut.convertToEntityAttribute("USD");

        // Then
        assertThat(result.value()).isEqualTo("USD");
    }

    @Test
    void convertToEntityAttributeReturnsNullForNull() {
        // Given
        var sut = new CurrencyCodeConverter();

        // When
        CurrencyCode result = sut.convertToEntityAttribute(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeNormalizesLowercase() {
        // Given
        var sut = new CurrencyCodeConverter();

        // When
        CurrencyCode result = sut.convertToEntityAttribute("eur");

        // Then
        assertThat(result.value()).isEqualTo("EUR");
    }

    @Test
    void convertToEntityAttributeRejectsInvalidData() {
        // Given
        var sut = new CurrencyCodeConverter();

        // When / Then
        assertThatThrownBy(() -> sut.convertToEntityAttribute("EU"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void roundtripPreservesValue() {
        // Given
        var sut = new CurrencyCodeConverter();
        var currencyCode = new CurrencyCode("CHF");

        // When
        CurrencyCode result = sut.convertToEntityAttribute(sut.convertToDatabaseColumn(currencyCode));

        // Then
        assertThat(result.value()).isEqualTo(currencyCode.value());
    }
}
