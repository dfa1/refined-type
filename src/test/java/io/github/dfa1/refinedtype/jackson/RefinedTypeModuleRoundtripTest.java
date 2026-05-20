package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dfa1.refinedtype.examples.Age;
import io.github.dfa1.refinedtype.examples.CountryCode;
import io.github.dfa1.refinedtype.examples.CurrencyCode;
import io.github.dfa1.refinedtype.examples.Email;
import io.github.dfa1.refinedtype.examples.Iban;
import io.github.dfa1.refinedtype.examples.Port;
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
        var sut = Email.of("user@example.com");

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("\"user@example.com\"");
    }

    @Test
    void serializesCountryCodeAsString() throws JsonProcessingException {
        // Given
        var sut = CountryCode.of("DE");

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("\"DE\"");
    }

    @Test
    void serializesCurrencyCodeAsString() throws JsonProcessingException {
        // Given
        var sut = CurrencyCode.of("EUR");

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("\"EUR\"");
    }

    @Test
    void serializesAgeAsNumber() throws JsonProcessingException {
        // Given
        var sut = Age.of(30);

        // When
        String result = MAPPER.writeValueAsString(sut);

        // Then
        assertThat(result).isEqualTo("30");
    }

    @Test
    void serializesPortAsNumber() throws JsonProcessingException {
        // Given
        var sut = Port.of(8080);

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
                Email.of("alice@example.com"),
                CountryCode.of("CH"),
                CurrencyCode.of("CHF"),
                Age.of(35),
                Port.of(9090)
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
                Email.of("bob@example.com"),
                CountryCode.of("US"),
                CurrencyCode.of("USD"),
                Age.of(28),
                Port.of(8080)
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

    // Value classes have no identity but their *reference projections* are still nullable.
    // The "cannot be null" guarantee applies to primitive (val) projections — a future
    // Valhalla feature not yet available in EA3. In the meantime, Jackson can bypass the
    // constructor entirely for null JSON tokens, leaving value-class fields null.
    record Person(String name, Age age, Iban iban) {}

    @Test
    void nullJsonTokenBypassesConstructorLeavingValueClassFieldNull() throws JsonProcessingException {
        // Given
        String json = "{\"name\":\"SuperMario\",\"age\":null,\"iban\":null}";

        // When
        Person result = MAPPER.readValue(json, Person.class);

        // Then — value classes are nullable references in EA3; constructor was never called
        assertThat(result.name()).isEqualTo("SuperMario");
        assertThat(result.age()).isNull();
        assertThat(result.iban()).isNull();
    }
}
