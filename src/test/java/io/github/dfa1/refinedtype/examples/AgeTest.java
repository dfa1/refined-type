package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedShort;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgeTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = Age.of(0);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void typicalAgeIsValid() {
        // Given
        var sut = Age.of(42);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(42);
    }

    @Test
    void maxIsValid() {
        // Given
        var sut = Age.of(Age.MAX);

        // When
        int result = sut.value();

        // Then
        assertThat(result).isEqualTo(150);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> Age.of(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aboveMaxRejected() {
        // When / Then
        assertThatThrownBy(() -> Age.of(Age.MAX + 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = Age.of(20);
        var other = Age.of(30);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = Age.of(50);
        var other = Age.of(50);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = Age.of(33);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Age(33)");
    }

    // ── equality ─────────────────────────────────────────────────────────────

    @Test
    void equalsSameValue() {
        // Given
        var sut = Age.of(30);
        var other = Age.of(30);

        // Then — value objects with identical fields are the same object (JEP 401)
        assertThat(sut).isEqualTo(other);
    }

    @Test
    void equalsDifferentValue() {
        // Given
        var sut = Age.of(30);
        var other = Age.of(31);

        // Then
        assertThat(sut).isNotEqualTo(other);
    }

    @Test
    void hashCodeConsistentForEqualValues() {
        // Given
        var sut = Age.of(30);
        var other = Age.of(30);

        // Then
        assertThat(sut.hashCode()).isEqualTo(other.hashCode());
    }

    // ── RefinedShort ─────────────────────────────────────────────────────────

    @Test
    void implementsRefinedShort() {
        // Given
        RefinedShort<?> sut = Age.of(18);

        // When
        short result = sut.value();

        // Then
        assertThat(result).isEqualTo((short) 18);
    }
}
