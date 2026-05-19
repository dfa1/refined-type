package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    void valueReturnsAddress() {
        // Given
        var sut = Email.of("user@example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("user@example.com");
    }

    @Test
    void blankRejected() {
        // When / Then
        assertThatThrownBy(() -> Email.of(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Email.of("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> Email.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void missingLocalPartRejected() {
        // Given
        String input = "@example.com";

        // When / Then
        assertThatThrownBy(() -> Email.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void multipleAtSignsRejected() {
        // Given
        String input = "a@b@example.com";

        // When / Then
        assertThatThrownBy(() -> Email.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void missingDomainDotRejected() {
        // Given
        String input = "user@localhost";

        // When / Then
        assertThatThrownBy(() -> Email.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyDomainRejected() {
        // Given
        String input = "user@";

        // When / Then
        assertThatThrownBy(() -> Email.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = Email.of("a@example.com");
        var other = Email.of("b@example.com");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsPositiveWhenLexicographicallyGreater() {
        // Given
        var sut = Email.of("b@example.com");
        var other = Email.of("a@example.com");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    @Test
    void compareToReturnsZeroForEqualAddresses() {
        // Given
        var sut = Email.of("a@example.com");
        var other = Email.of("a@example.com");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void toStringFormat() {
        // Given
        var sut = Email.of("user@example.com");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Email(user@example.com)");
    }

    // ── equality ─────────────────────────────────────────────────────────────

    @Test
    void equalsSameValue() {
        // Given
        var sut = Email.of("user@example.com");
        var other = Email.of("user@example.com");

        // Then — String-backed value objects with identical fields are the same object (JEP 401)
        assertThat(sut).isEqualTo(other);
    }

    @Test
    void equalsDifferentValue() {
        // Given
        var sut = Email.of("a@example.com");
        var other = Email.of("b@example.com");

        // Then
        assertThat(sut).isNotEqualTo(other);
    }

    @Test
    void hashCodeConsistentForEqualValues() {
        // Given
        var sut = Email.of("user@example.com");
        var other = Email.of("user@example.com");

        // Then
        assertThat(sut.hashCode()).isEqualTo(other.hashCode());
    }

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = Email.of("user@example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("user@example.com");
    }
}
