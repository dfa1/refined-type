package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedInt;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PortTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = new Port(0);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxValueIsValid() {
        // Given
        var sut = new Port(Port.MAX_VALUE);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(65_535);
    }

    @Test
    void httpPortIsValid() {
        // Given
        var sut = new Port(80);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(80);
    }

    @Test
    void negativeRejected() {
        // Given
        int input = -1;

        // When / Then
        assertThatThrownBy(() -> new Port(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aboveMaxRejected() {
        // Given
        int input = 65_536;

        // When / Then
        assertThatThrownBy(() -> new Port(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── fromString ──────────────────────────────────────────────────────────

    @Test
    void fromStringValid() {
        // Given / When
        Port result = Port.fromString("443");

        // Then
        assertThat(result.value()).isEqualTo(443);
    }

    @Test
    void fromStringOutOfRangeThrows() {
        // When / Then
        assertThatThrownBy(() -> Port.fromString("70000"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fromStringNonNumericThrows() {
        // When / Then
        assertThatThrownBy(() -> Port.fromString("http"))
                .isInstanceOf(NumberFormatException.class);
    }

    // ── classification ──────────────────────────────────────────────────────

    @Test
    void wellKnownTrueForLowPort() {
        // Given
        var sut = new Port(22);

        // When
        boolean result = sut.isWellKnown();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void wellKnownFalseAboveBoundary() {
        // Given
        var sut = new Port(1024);

        // When
        boolean result = sut.isWellKnown();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void ephemeralTrueForHighPort() {
        // Given
        var sut = new Port(60_000);

        // When
        boolean result = sut.isEphemeral();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void ephemeralFalseBelowBoundary() {
        // Given
        var sut = new Port(49_151);

        // When
        boolean result = sut.isEphemeral();

        // Then
        assertThat(result).isFalse();
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = new Port(80);
        var other = new Port(443);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new Port(8080);
        var other = new Port(8080);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringFormat() {
        // Given
        var sut = new Port(443);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Port(443)");
    }

    // ── RefinedInt ──────────────────────────────────────────────────────────

    @Test
    void implementsRefinedInt() {
        // Given
        RefinedInt sut = new Port(22);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(22);
    }
}
