package io.github.dfa1.refinedtypes.jpa;

import io.github.dfa1.refinedtypes.examples.Age;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgeConverterTest {

    @Test
    void convertToDatabaseColumnReturnsInt() {
        // Given
        var sut = new AgeConverter();
        var age = new Age(30);

        // When
        Integer result = sut.convertToDatabaseColumn(age);

        // Then
        assertThat(result).isEqualTo(30);
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNull() {
        // Given
        var sut = new AgeConverter();

        // When
        Integer result = sut.convertToDatabaseColumn(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeReturnsAge() {
        // Given
        var sut = new AgeConverter();

        // When
        Age result = sut.convertToEntityAttribute(42);

        // Then
        assertThat(result.value()).isEqualTo(42);
    }

    @Test
    void convertToEntityAttributeReturnsNullForNull() {
        // Given
        var sut = new AgeConverter();

        // When
        Age result = sut.convertToEntityAttribute(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeRejectsOutOfRangeValue() {
        // Given
        var sut = new AgeConverter();

        // When / Then
        assertThatThrownBy(() -> sut.convertToEntityAttribute(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void roundtripPreservesValue() {
        // Given
        var sut = new AgeConverter();
        var age = new Age(25);

        // When
        Age result = sut.convertToEntityAttribute(sut.convertToDatabaseColumn(age));

        // Then
        assertThat(result.value()).isEqualTo(age.value());
    }
}
