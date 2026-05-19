package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BicTest {

    // ── construction — 8-char input ─────────────────────────────────────────

    @Test
    void eightCharInputExpandedToEleven() {
        // Given / When
        var sut = new Bic("DEUTDEDB");

        // Then
        assertThat(sut.value()).isEqualTo("DEUTDEDBXXX");
    }

    @Test
    void eightCharInputCaseInsensitive() {
        // Given / When
        var sut = new Bic("deutdedb");

        // Then
        assertThat(sut.value()).isEqualTo("DEUTDEDBXXX");
    }

    // ── construction — 11-char input ────────────────────────────────────────

    @Test
    void elevenCharInputStoredAsIs() {
        // Given / When
        var sut = new Bic("DEUTDEDBFRA");

        // Then
        assertThat(sut.value()).isEqualTo("DEUTDEDBFRA");
    }

    @Test
    void elevenCharWithXxxStoredAsIs() {
        // Given / When
        var sut = new Bic("DEUTDEDBXXX");

        // Then
        assertThat(sut.value()).isEqualTo("DEUTDEDBXXX");
    }

    @Test
    void alphanumericBranchCode() {
        // Given / When
        var sut = new Bic("UBSWCHZH80A");

        // Then
        assertThat(sut.value()).isEqualTo("UBSWCHZH80A");
    }

    // ── construction — rejection ────────────────────────────────────────────

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> new Bic(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void wrongLengthRejected() {
        // When / Then
        assertThatThrownBy(() -> new Bic("DEUT"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void numericBankCodeRejected() {
        // When / Then — bank code must be letters only
        assertThatThrownBy(() -> new Bic("1234DEBB"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void numericCountryCodeRejected() {
        // When / Then — country code must be letters only
        assertThatThrownBy(() -> new Bic("DEUT12BB"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── part extraction ─────────────────────────────────────────────────────

    @Test
    void bankCodeExtracted() {
        // Given
        var sut = new Bic("DEUTDEDBFRA");

        // When
        String result = sut.bankCode();

        // Then
        assertThat(result).isEqualTo("DEUT");
    }

    @Test
    void countryExtracted() {
        // Given
        var sut = new Bic("DEUTDEDBFRA");

        // When
        CountryCode result = sut.country();

        // Then
        assertThat(result.value()).isEqualTo("DE");
    }

    @Test
    void locationCodeExtracted() {
        // Given
        var sut = new Bic("DEUTDEDBFRA");

        // When
        String result = sut.locationCode();

        // Then
        assertThat(result).isEqualTo("DB");
    }

    @Test
    void branchCodeExtracted() {
        // Given
        var sut = new Bic("DEUTDEDBFRA");

        // When
        String result = sut.branchCode();

        // Then
        assertThat(result).isEqualTo("FRA");
    }

    @Test
    void branchCodeXxxForExpandedEightChar() {
        // Given
        var sut = new Bic("DEUTDEDB");

        // When
        String result = sut.branchCode();

        // Then
        assertThat(result).isEqualTo("XXX");
    }

    // ── isPrimaryOffice ─────────────────────────────────────────────────────

    @Test
    void isPrimaryOfficeTrueForXxx() {
        // Given
        var sut = new Bic("DEUTDEDBXXX");

        // When
        boolean result = sut.isPrimaryOffice();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void isPrimaryOfficeFalseForBranch() {
        // Given
        var sut = new Bic("DEUTDEDBFRA");

        // When
        boolean result = sut.isPrimaryOffice();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void isPrimaryOfficeTrueForExpandedEightChar() {
        // Given
        var sut = new Bic("DEUTDEDB");

        // When
        boolean result = sut.isPrimaryOffice();

        // Then
        assertThat(result).isTrue();
    }

    // ── toBic8 ──────────────────────────────────────────────────────────────

    @Test
    void toBic8StripsXxxSuffix() {
        // Given
        var sut = new Bic("DEUTDEDBXXX");

        // When
        String result = sut.toBic8();

        // Then
        assertThat(result).isEqualTo("DEUTDEDB");
    }

    @Test
    void toBic8ReturnsFull11ForBranch() {
        // Given
        var sut = new Bic("DEUTDEDBFRA");

        // When
        String result = sut.toBic8();

        // Then
        assertThat(result).isEqualTo("DEUTDEDBFRA");
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesCanonicalValue() {
        // Given
        var sut = new Bic("DEUTDEDB");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Bic(DEUTDEDBXXX)");
    }

    // ── RefinedString ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString<?> sut = new Bic("UBSWCHZH");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("UBSWCHZHXXX");
    }
}
