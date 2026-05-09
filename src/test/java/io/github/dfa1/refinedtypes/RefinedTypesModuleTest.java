package io.github.dfa1.refinedtypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefinedTypesModuleTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new RefinedTypesModule());

    // package-private constructor — used to verify MethodHandles.privateLookupIn path
    static value class InternalCode implements RefinedString {
        private final String value;

        InternalCode(String value) { // package-private
            if (value == null || value.isBlank()) throw new IllegalArgumentException("blank");
            this.value = value;
        }

        @Override public String value() { return value; }
    }

    // ── RefinedString serialization ──────────────────────────────────────────

    @Test
    void isinSerializedAsJsonString() throws Exception {
        // Given
        var sut = new Isin("US0378331005");

        // When
        String result = mapper.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("\"US0378331005\"");
    }

    @Test
    void emailSerializedAsJsonString() throws Exception {
        // Given
        var sut = new Email("user@example.com");

        // When
        String result = mapper.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("\"user@example.com\"");
    }

    @Test
    void countrySerializedAsJsonString() throws Exception {
        // Given
        var sut = new Country("DE");

        // When
        String result = mapper.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("\"DE\"");
    }

    // ── RefinedString deserialization ────────────────────────────────────────

    @Test
    void isinDeserializedFromJsonString() throws Exception {
        // Given
        String json = "\"US0378331005\"";

        // When
        Isin result = mapper.readValue(json, Isin.class);

        // Then
        assertThat(result.value()).isEqualTo("US0378331005");
    }

    @Test
    void emailDeserializedFromJsonString() throws Exception {
        // Given
        String json = "\"user@example.com\"";

        // When
        Email result = mapper.readValue(json, Email.class);

        // Then
        assertThat(result.value()).isEqualTo("user@example.com");
    }

    @Test
    void countryDeserializedFromJsonString() throws Exception {
        // Given
        String json = "\"DE\"";

        // When
        Country result = mapper.readValue(json, Country.class);

        // Then
        assertThat(result.value()).isEqualTo("DE");
    }

    @Test
    void packagePrivateConstructorDeserializedViaSetAccessible() throws Exception {
        // Given
        String json = "\"INTERNAL\"";

        // When
        InternalCode result = mapper.readValue(json, InternalCode.class);

        // Then
        assertThat(result.value()).isEqualTo("INTERNAL");
    }

    @Test
    void invalidIsinDeserializationThrowsInvalidFormatException() {
        // Given
        String json = "\"NOT-AN-ISIN\"";

        // When / Then
        assertThatThrownBy(() -> mapper.readValue(json, Isin.class))
                .isInstanceOf(InvalidFormatException.class);
    }

    // ── RefinedInt serialization ─────────────────────────────────────────────

    @Test
    void positiveIntSerializedAsJsonNumber() throws Exception {
        // Given
        var sut = new PositiveInt(42);

        // When
        String result = mapper.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("42");
    }

    // ── RefinedInt deserialization ───────────────────────────────────────────

    @Test
    void positiveIntDeserializedFromJsonNumber() throws Exception {
        // Given
        String json = "42";

        // When
        PositiveInt result = mapper.readValue(json, PositiveInt.class);

        // Then
        assertThat(result.value()).isEqualTo(42);
    }

    @Test
    void invalidPositiveIntDeserializationThrowsInvalidFormatException() {
        // Given
        String json = "-1";

        // When / Then
        assertThatThrownBy(() -> mapper.readValue(json, PositiveInt.class))
                .isInstanceOf(InvalidFormatException.class);
    }
}
