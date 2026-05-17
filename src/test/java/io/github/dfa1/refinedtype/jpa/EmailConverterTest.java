package io.github.dfa1.refinedtype.jpa;

import io.github.dfa1.refinedtype.examples.Email;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailConverterTest {

    @Test
    void convertToDatabaseColumnReturnsEmailString() {
        // Given
        var sut = new EmailConverter();
        var email = new Email("user@example.com");

        // When
        String result = sut.convertToDatabaseColumn(email);

        // Then
        assertThat(result).isEqualTo("user@example.com");
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNull() {
        // Given
        var sut = new EmailConverter();

        // When
        String result = sut.convertToDatabaseColumn(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeReturnsEmail() {
        // Given
        var sut = new EmailConverter();

        // When
        Email result = sut.convertToEntityAttribute("user@example.com");

        // Then
        assertThat(result.value()).isEqualTo("user@example.com");
    }

    @Test
    void convertToEntityAttributeReturnsNullForNull() {
        // Given
        var sut = new EmailConverter();

        // When
        Email result = sut.convertToEntityAttribute(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeRejectsInvalidData() {
        // Given
        var sut = new EmailConverter();

        // When / Then
        assertThatThrownBy(() -> sut.convertToEntityAttribute("not-an-email"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void roundtripPreservesValue() {
        // Given
        var sut = new EmailConverter();
        var email = new Email("roundtrip@example.com");

        // When
        Email result = sut.convertToEntityAttribute(sut.convertToDatabaseColumn(email));

        // Then
        assertThat(result.value()).isEqualTo(email.value());
    }
}
