package io.github.dfa1.refinedtype.examples;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Mod97Test {

    // ── digits only ──────────────────────────────────────────────────────────

    @Test
    void zeroInputReturnsZero() {
        // Given
        var input = "0";

        // When
        int result = Mod97.compute(input);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void emptyStringReturnsZero() {
        // Given
        var input = "";

        // When
        int result = Mod97.compute(input);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void exactMultipleOfNinetySeven() {
        // Given
        var input = "97"; // 97 mod 97 = 0

        // When
        int result = Mod97.compute(input);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void doubleMultipleOfNinetySeven() {
        // Given
        var input = "194"; // 194 = 2 × 97

        // When
        int result = Mod97.compute(input);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void remainderOneForNinetyEight() {
        // Given
        var input = "98"; // 98 mod 97 = 1

        // When
        int result = Mod97.compute(input);

        // Then
        assertThat(result).isEqualTo(1);
    }

    // ── letter substitution ──────────────────────────────────────────────────

    @Test
    void letterAMapsToTen() {
        // Given
        var input = "A"; // A → 10; 10 mod 97 = 10

        // When
        int result = Mod97.compute(input);

        // Then
        assertThat(result).isEqualTo(10);
    }

    @Test
    void letterZMapsToThirtyFive() {
        // Given
        var input = "Z"; // Z → 35; 35 mod 97 = 35

        // When
        int result = Mod97.compute(input);

        // Then
        assertThat(result).isEqualTo(35);
    }

    @Test
    void letterBMapsToEleven() {
        // Given
        var input = "B"; // B → 11

        // When
        int result = Mod97.compute(input);

        // Then
        assertThat(result).isEqualTo(11);
    }

    // ── mixed input ──────────────────────────────────────────────────────────

    @Test
    void mixedLettersAndDigitsComputedCorrectly() {
        // Given — "A0" → numeric "100"; 100 mod 97 = 3
        var input = "A0";

        // When
        int result = Mod97.compute(input);

        // Then
        assertThat(result).isEqualTo(3);
    }

    @Test
    void consecutiveLettersExpandCorrectly() {
        // Given — "AB" → numeric "1011"; 1011 mod 97 = 1011 - 10×97 = 1011 - 970 = 41
        var input = "AB";

        // When
        int result = Mod97.compute(input);

        // Then
        assertThat(result).isEqualTo(41);
    }

    // ── IBAN integration ─────────────────────────────────────────────────────

    @Test
    void validIbanRearrangementReturnsOne() {
        // Given — ISO 13616: move first 4 chars to end, substitute letters, check mod97 = 1
        // DE89370400440532013000 → "370400440532013000DE89" → "3704004405320130001314​89"
        var rearranged = "370400440532013000131489";

        // When
        int result = Mod97.compute(rearranged);

        // Then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void validGbIbanRearrangementReturnsOne() {
        // Given — GB82WEST12345698765432 → "WEST12345698765432GB82"
        // W=32 E=14 S=28 T=29 G=16 B=11 → "3214282912345698765432161182"
        var rearranged = "3214282912345698765432161182";

        // When
        int result = Mod97.compute(rearranged);

        // Then
        assertThat(result).isEqualTo(1);
    }
}
