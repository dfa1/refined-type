package io.github.dfa1.refinedtype.jpa;

import io.github.dfa1.refinedtype.examples.Port;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PortConverterTest {

    @Test
    void convertToDatabaseColumnReturnsInt() {
        // Given
        var sut = new PortConverter();
        var port = Port.of(8080);

        // When
        Integer result = sut.convertToDatabaseColumn(port);

        // Then
        assertThat(result).isEqualTo(8080);
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNull() {
        // Given
        var sut = new PortConverter();

        // When
        Integer result = sut.convertToDatabaseColumn(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeReturnsPort() {
        // Given
        var sut = new PortConverter();

        // When
        Port result = sut.convertToEntityAttribute(443);

        // Then
        assertThat(result.value()).isEqualTo(443);
    }

    @Test
    void convertToEntityAttributeReturnsNullForNull() {
        // Given
        var sut = new PortConverter();

        // When
        Port result = sut.convertToEntityAttribute(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeRejectsOutOfRangePort() {
        // Given
        var sut = new PortConverter();

        // When / Then
        assertThatThrownBy(() -> sut.convertToEntityAttribute(70_000))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void roundtripPreservesValue() {
        // Given
        var sut = new PortConverter();
        var port = Port.of(8080);

        // When
        Port result = sut.convertToEntityAttribute(sut.convertToDatabaseColumn(port));

        // Then
        assertThat(result.value()).isEqualTo(port.value());
    }
}
