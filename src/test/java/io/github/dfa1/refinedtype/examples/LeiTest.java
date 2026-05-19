package io.github.dfa1.refinedtype.examples;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeiTest {

    // Known-valid LEIs from public registries
    private static final String DEUTSCHE_BANK = "7H6GLXDRUGQFU57RNE97";
    private static final String JPMORGAN      = "HWUPKR0MPOU8FGXBT394";

    // --- construction (parse) ---

    @Test
    void validLeiAccepted() {
        // Given / When
        var sut = Lei.of(DEUTSCHE_BANK);

        // Then
        assertThat(sut.value()).isEqualTo(DEUTSCHE_BANK);
    }

    @Test
    void lowercaseNormalizedToUppercase() {
        // Given / When
        var sut = Lei.of(DEUTSCHE_BANK.toLowerCase());

        // Then
        assertThat(sut.value()).isEqualTo(DEUTSCHE_BANK);
    }

    @Test
    void tooShortRejected() {
        assertThatThrownBy(() -> Lei.of("7H6GLXDRUGQFU57RNE9"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("20 characters");
    }

    @Test
    void tooLongRejected() {
        assertThatThrownBy(() -> Lei.of("7H6GLXDRUGQFU57RNE970"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("20 characters");
    }

    @Test
    void nonAlphanumericRejected() {
        assertThatThrownBy(() -> Lei.of("7H6GLXDRUGQFU57RN-97"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void letterCheckDigitsRejected() {
        // Check digits (last 2 chars) must be numeric
        assertThatThrownBy(() -> Lei.of("7H6GLXDRUGQFU57RNEAB"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- factory Lei.ofPrefix ---

    @Test
    void ofPrefixComputesValidChecksum() {
        // Given: Deutsche Bank prefix without check digits
        String prefix = DEUTSCHE_BANK.substring(0, 18);

        // When
        var sut = Lei.ofPrefix(prefix);

        // Then
        assertThat(sut.value()).isEqualTo(DEUTSCHE_BANK);
    }

    @Test
    void ofPrefixJpMorganComputesValidChecksum() {
        // Given
        String prefix = JPMORGAN.substring(0, 18);

        // When
        var sut = Lei.ofPrefix(prefix);

        // Then
        assertThat(sut.value()).isEqualTo(JPMORGAN);
    }

    @Test
    void ofPrefixRejectsTooShortPrefix() {
        assertThatThrownBy(() -> Lei.ofPrefix("7H6GLXDRUGQFU57RN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("18 characters");
    }

    @Test
    void ofPrefixRejectsNonAlphanumericPrefix() {
        assertThatThrownBy(() -> Lei.ofPrefix("7H6GLXDRUGQFU57RN-"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- accessors ---

    @Test
    void louIsFirst4Chars() {
        // Given
        var sut = Lei.of(DEUTSCHE_BANK);

        // When
        String result = sut.lou();

        // Then
        assertThat(result).isEqualTo("7H6G");
    }

    @Test
    void entityCodeIs14Chars() {
        // Given
        var sut = Lei.of(DEUTSCHE_BANK);

        // When
        String result = sut.entityCode();

        // Then
        assertThat(result).hasSize(14);
        assertThat(result).isEqualTo("LXDRUGQFU57RNE");
    }

    @Test
    void checkDigitsParsedCorrectly() {
        // Given
        var sut = Lei.of(DEUTSCHE_BANK);

        // When
        String result = sut.checkDigits();

        // Then
        assertThat(result).isEqualTo("97");
    }

    // --- checksum validation ---

    @Test
    void corruptedLeiRejected() {
        // Given: valid shape but wrong check digits
        String corrupted = "7H6GLXDRUGQFU57RNE07"; // was ...97

        // When / Then
        assertThatThrownBy(() -> Lei.of(corrupted))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("checksum invalid");
    }

    // --- comparison ---

    @Test
    void compareToOrdering() {
        // Given
        var a = Lei.of(DEUTSCHE_BANK);
        var b = Lei.of(JPMORGAN);

        // When / Then
        assertThat(a.compareTo(b)).isNotEqualTo(0);
        assertThat(a.compareTo(a)).isEqualTo(0);
    }

    @Test
    void toStringContainsValue() {
        var sut = Lei.of(DEUTSCHE_BANK);
        assertThat(sut.toString()).contains(DEUTSCHE_BANK);
    }
}
