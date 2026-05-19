package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IbanTest {

    // Real-world IBANs used across tests
    private static final String DE_IBAN = "DE89370400440532013000";
    private static final String GB_IBAN = "GB82WEST12345698765432";

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void validGermanIbanAccepted() {
        // Given / When
        var sut = new Iban(DE_IBAN);

        // Then
        assertThat(sut.value()).isEqualTo(DE_IBAN);
    }

    @Test
    void validUkIbanAccepted() {
        // Given / When
        var sut = new Iban(GB_IBAN);

        // Then
        assertThat(sut.value()).isEqualTo(GB_IBAN);
    }

    @Test
    void spacesStrippedOnInput() {
        // Given — human-readable grouped form
        var sut = new Iban("DE89 3704 0044 0532 0130 00");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo(DE_IBAN);
    }

    @Test
    void lowercaseNormalisedToUppercase() {
        // Given / When
        var sut = new Iban(DE_IBAN.toLowerCase());

        // Then
        assertThat(sut.value()).isEqualTo(DE_IBAN);
    }

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> new Iban(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooShortRejected() {
        // When / Then — fewer than 15 chars
        assertThatThrownBy(() -> new Iban("DE8937"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void wrongChecksumRejected() {
        // When / Then — valid structure but wrong check digits
        assertThatThrownBy(() -> new Iban("DE00370400440532013000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("checksum");
    }

    @Test
    void nonAlphanumericBbanRejected() {
        // When / Then
        assertThatThrownBy(() -> new Iban("DE89!!!!!!!!!!!!!!!!!!!"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── of() factory ────────────────────────────────────────────────────────

    @Test
    void factoryComputesCorrectCheckDigits() {
        // Given — BBAN from the known German IBAN
        var result = Iban.of(CountryCode.of("DE"), "370400440532013000");

        // Then — check digits must match the known value
        assertThat(result.checkDigits()).isEqualTo("89");
        assertThat(result.value()).isEqualTo(DE_IBAN);
    }

    @Test
    void factoryRoundtrip() {
        // Given — build from country + BBAN, then re-parse
        Iban built = Iban.of(CountryCode.of("GB"), "WEST12345698765432");

        // When
        var reparsed = new Iban(built.value());

        // Then
        assertThat(reparsed.value()).isEqualTo(built.value());
    }

    @Test
    void factoryStripsSpacesFromBban() {
        // Given
        var result = Iban.of(CountryCode.of("DE"), "3704 0044 0532 0130 00");

        // Then
        assertThat(result.value()).isEqualTo(DE_IBAN);
    }

    @Test
    void factoryRejectsNullBban() {
        // When / Then
        assertThatThrownBy(() -> Iban.of(CountryCode.of("DE"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void factoryRejectsNonAlphanumericBban() {
        // When / Then
        assertThatThrownBy(() -> Iban.of(CountryCode.of("DE"), "!invalid!"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── part extraction ─────────────────────────────────────────────────────

    @Test
    void countryExtracted() {
        // Given
        var sut = new Iban(DE_IBAN);

        // When
        CountryCode result = sut.country();

        // Then
        assertThat(result.value()).isEqualTo("DE");
    }

    @Test
    void checkDigitsExtracted() {
        // Given
        var sut = new Iban(DE_IBAN);

        // When
        String result = sut.checkDigits();

        // Then
        assertThat(result).isEqualTo("89");
    }

    @Test
    void bbanExtracted() {
        // Given
        var sut = new Iban(DE_IBAN);

        // When
        String result = sut.bban();

        // Then
        assertThat(result).isEqualTo("370400440532013000");
    }

    @Test
    void ukPartsExtracted() {
        // Given
        var sut = new Iban(GB_IBAN);

        // When / Then
        assertThat(sut.country().value()).isEqualTo("GB");
        assertThat(sut.checkDigits()).isEqualTo("82");
        assertThat(sut.bban()).isEqualTo("WEST12345698765432");
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = new Iban(DE_IBAN);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Iban(" + DE_IBAN + ")");
    }

    // ── RefinedString ─────────────────────────────────────────────────────────

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString<?> sut = new Iban(GB_IBAN);

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo(GB_IBAN);
    }
}
