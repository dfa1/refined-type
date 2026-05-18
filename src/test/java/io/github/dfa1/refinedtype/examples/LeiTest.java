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
        var sut = new Lei(DEUTSCHE_BANK);

        // Then
        assertThat(sut.value()).isEqualTo(DEUTSCHE_BANK);
    }

    @Test
    void lowercaseNormalizedToUppercase() {
        // Given / When
        var sut = new Lei(DEUTSCHE_BANK.toLowerCase());

        // Then
        assertThat(sut.value()).isEqualTo(DEUTSCHE_BANK);
    }

    @Test
    void tooShortRejected() {
        assertThatThrownBy(() -> new Lei("7H6GLXDRUGQFU57RNE9"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("20 characters");
    }

    @Test
    void tooLongRejected() {
        assertThatThrownBy(() -> new Lei("7H6GLXDRUGQFU57RNE970"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("20 characters");
    }

    @Test
    void nonAlphanumericRejected() {
        assertThatThrownBy(() -> new Lei("7H6GLXDRUGQFU57RN-97"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void letterCheckDigitsRejected() {
        // Check digits (last 2 chars) must be numeric
        assertThatThrownBy(() -> new Lei("7H6GLXDRUGQFU57RNEAB"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- factory Lei.of ---

    @Test
    void ofComputesValidChecksum() {
        // Given: Deutsche Bank prefix without check digits
        String prefix = DEUTSCHE_BANK.substring(0, 18);

        // When
        var sut = Lei.of(prefix);

        // Then
        assertThat(sut.value()).isEqualTo(DEUTSCHE_BANK);
        assertThat(sut.isChecksumValid()).isTrue();
    }

    @Test
    void ofJpMorganComputesValidChecksum() {
        // Given
        String prefix = JPMORGAN.substring(0, 18);

        // When
        var sut = Lei.of(prefix);

        // Then
        assertThat(sut.value()).isEqualTo(JPMORGAN);
        assertThat(sut.isChecksumValid()).isTrue();
    }

    @Test
    void ofRejectsTooShortPrefix() {
        assertThatThrownBy(() -> Lei.of("7H6GLXDRUGQFU57RN"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("18 characters");
    }

    @Test
    void ofRejectsNonAlphanumericPrefix() {
        assertThatThrownBy(() -> Lei.of("7H6GLXDRUGQFU57RN-"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // --- accessors ---

    @Test
    void louIsFirst4Chars() {
        // Given
        var sut = new Lei(DEUTSCHE_BANK);

        // When
        String result = sut.lou();

        // Then
        assertThat(result).isEqualTo("7H6G");
    }

    @Test
    void entityCodeIs14Chars() {
        // Given
        var sut = new Lei(DEUTSCHE_BANK);

        // When
        String result = sut.entityCode();

        // Then
        assertThat(result).hasSize(14);
        assertThat(result).isEqualTo("LXDRUGQFU57RNE");
    }

    @Test
    void checkDigitsParsedCorrectly() {
        // Given
        var sut = new Lei(DEUTSCHE_BANK);

        // When
        int result = sut.checkDigits();

        // Then
        assertThat(result).isEqualTo(97);
    }

    // --- checksum validation ---

    @Test
    void parsedLeiChecksumValid() {
        assertThat(new Lei(DEUTSCHE_BANK).isChecksumValid()).isTrue();
        assertThat(new Lei(JPMORGAN).isChecksumValid()).isTrue();
    }

    @Test
    void corruptedLeiFailsChecksum() {
        // Given: flip one digit in the entity part
        String corrupted = "7H6GLXDRUGQFU57RNE07"; // was ...97

        // When
        var sut = new Lei(corrupted);

        // Then: shape is valid but checksum fails
        assertThat(sut.isChecksumValid()).isFalse();
    }

    // --- comparison ---

    @Test
    void compareToOrdering() {
        // Given
        var a = new Lei(DEUTSCHE_BANK);
        var b = new Lei(JPMORGAN);

        // When / Then
        assertThat(a.compareTo(b)).isNotEqualTo(0);
        assertThat(a.compareTo(a)).isEqualTo(0);
    }

    @Test
    void toStringContainsValue() {
        var sut = new Lei(DEUTSCHE_BANK);
        assertThat(sut.toString()).contains(DEUTSCHE_BANK);
    }
}
