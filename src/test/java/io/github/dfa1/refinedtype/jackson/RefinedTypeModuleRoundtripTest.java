package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dfa1.refinedtype.examples.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefinedTypeModuleRoundtripTest {

    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new RefinedTypeModule());

    record PersonDto(Email email, CountryCode country, CurrencyCode currency, Age age, Port port) {}

    @Test
    void serializesEmailAsString() throws JsonProcessingException {
        // Given
        var sut = new Email("user@example.com");

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("\"user@example.com\"");
    }

    @Test
    void serializesCountryCodeAsString() throws JsonProcessingException {
        // Given
        var sut = new CountryCode("DE");

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("\"DE\"");
    }

    @Test
    void serializesCurrencyCodeAsString() throws JsonProcessingException {
        // Given
        var sut = new CurrencyCode("EUR");

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("\"EUR\"");
    }

    @Test
    void serializesAgeAsNumber() throws JsonProcessingException {
        // Given
        var sut = new Age(30);

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("30");
    }

    @Test
    void serializesPortAsNumber() throws JsonProcessingException {
        // Given
        var sut = new Port(8080);

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("8080");
    }

    @Test
    void deserializesEmail() throws JsonProcessingException {
        // Given / When
        Email result = MAPPER.readValue("\"user@example.com\"", Email.class);

        // Then
        assertThat(result.value()).isEqualTo("user@example.com");
    }

    @Test
    void deserializesCountryCode() throws JsonProcessingException {
        // Given / When
        CountryCode result = MAPPER.readValue("\"FR\"", CountryCode.class);

        // Then
        assertThat(result.value()).isEqualTo("FR");
    }

    @Test
    void deserializesCurrencyCode() throws JsonProcessingException {
        // Given / When
        CurrencyCode result = MAPPER.readValue("\"USD\"", CurrencyCode.class);

        // Then
        assertThat(result.value()).isEqualTo("USD");
    }

    @Test
    void deserializesAge() throws JsonProcessingException {
        // Given / When
        Age result = MAPPER.readValue("42", Age.class);

        // Then
        assertThat(result.value()).isEqualTo((short) 42);
    }

    @Test
    void deserializesPort() throws JsonProcessingException {
        // Given / When
        Port result = MAPPER.readValue("443", Port.class);

        // Then
        assertThat(result.value()).isEqualTo(443);
    }

    @Test
    void fullRoundtripPreservesAllFields() throws JsonProcessingException {
        // Given
        var dto = new PersonDto(
                new Email("alice@example.com"),
                new CountryCode("CH"),
                new CurrencyCode("CHF"),
                new Age(35),
                new Port(9090)
        );

        // When
        String json = MAPPER.writeValueAsString(dto);
        PersonDto result = MAPPER.readValue(json, PersonDto.class);

        // Then
        assertThat(result.email().value()).isEqualTo("alice@example.com");
        assertThat(result.country().value()).isEqualTo("CH");
        assertThat(result.currency().value()).isEqualTo("CHF");
        assertThat(result.age().value()).isEqualTo((short) 35);
        assertThat(result.port().value()).isEqualTo(9090);
    }

    @Test
    void serializationProducesExpectedJsonShape() throws JsonProcessingException {
        // Given
        var dto = new PersonDto(
                new Email("bob@example.com"),
                new CountryCode("US"),
                new CurrencyCode("USD"),
                new Age(28),
                new Port(8080)
        );

        // When
        String result = MAPPER.writeValueAsString(dto);

        // Then
        assertThat(result)
                .contains("\"email\":\"bob@example.com\"")
                .contains("\"country\":\"US\"")
                .contains("\"currency\":\"USD\"")
                .contains("\"age\":28")
                .contains("\"port\":8080");
    }

    @Test
    void deserializationRejectsInvalidEmail() {
        // Given / When / Then
        assertThatThrownBy(() -> MAPPER.readValue("\"not-an-email\"", Email.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("@");
    }

    @Test
    void deserializationRejectsOutOfRangeAge() {
        // Given / When / Then
        assertThatThrownBy(() -> MAPPER.readValue("999", Age.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── previously-unregistered types (generic deserializer) ────────────────

    @Test
    void serializesProbabilityAsNumber() throws JsonProcessingException {
        // Given
        var sut = new Probability(0.75f);

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("0.75");
    }

    @Test
    void deserializesProbability() throws JsonProcessingException {
        // Given / When
        Probability result = MAPPER.readValue("0.5", Probability.class);

        // Then
        assertThat(result.value()).isEqualTo(0.5f);
    }

    @Test
    void serializesLatitudeAsNumber() throws JsonProcessingException {
        // Given
        var sut = new Latitude(48.8566);

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("48.8566");
    }

    @Test
    void deserializesLatitude() throws JsonProcessingException {
        // Given / When
        Latitude result = MAPPER.readValue("51.5", Latitude.class);

        // Then
        assertThat(result.value()).isEqualTo(51.5);
    }

    @Test
    void serializesSwissValorNumberAsNumber() throws JsonProcessingException {
        // Given
        var sut = new SwissValorNumber(1_222_171);

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("1222171");
    }

    @Test
    void deserializesSwissValorNumber() throws JsonProcessingException {
        // Given / When
        SwissValorNumber result = MAPPER.readValue("1222171", SwissValorNumber.class);

        // Then
        assertThat(result.value()).isEqualTo(1_222_171);
    }

    @Test
    void serializesSizeAsNumber() throws JsonProcessingException {
        // Given
        var sut = new Size(1024L);

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("1024");
    }

    @Test
    void deserializesSize() throws JsonProcessingException {
        // Given / When
        Size result = MAPPER.readValue("4096", Size.class);

        // Then
        assertThat(result.value()).isEqualTo(4096L);
    }

    @Test
    void deserializationRejectsInvalidProbability() {
        // Given / When / Then
        assertThatThrownBy(() -> MAPPER.readValue("1.5", Probability.class))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
