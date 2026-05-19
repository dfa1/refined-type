package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedFloat;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProbabilityTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zeroIsValid() {
        // Given
        var sut = Probability.of(0f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void oneIsValid() {
        // Given
        var sut = Probability.of(1f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(1f);
    }

    @Test
    void midRangeIsValid() {
        // Given
        var sut = Probability.of(0.5f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(0.5f);
    }

    @Test
    void negativeRejected() {
        // When / Then
        assertThatThrownBy(() -> Probability.of(-0.0001f))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void aboveOneRejected() {
        // When / Then
        assertThatThrownBy(() -> Probability.of(1.0001f))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void percentageScaleRejected() {
        // 50 belongs to Percentage, not Probability — exactly the bug class this type prevents
        // When / Then
        assertThatThrownBy(() -> Probability.of(50f))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nanRejected() {
        // When / Then
        assertThatThrownBy(() -> Probability.of(Float.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void positiveInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> Probability.of(Float.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeInfinityRejected() {
        // When / Then
        assertThatThrownBy(() -> Probability.of(Float.NEGATIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── constants ───────────────────────────────────────────────────────────

    @Test
    void zeroConstant() {
        // Given / When
        Probability result = Probability.ZERO;

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void oneConstant() {
        // Given / When
        Probability result = Probability.ONE;

        // Then
        assertThat(result.value()).isEqualTo(1f);
    }

    // ── complement ──────────────────────────────────────────────────────────

    @Test
    void complementOfQuarterIsThreeQuarters() {
        // Given
        var sut = Probability.of(0.25f);

        // When
        Probability result = sut.complement();

        // Then
        assertThat(result.value()).isEqualTo(0.75f);
    }

    @Test
    void complementOfZeroIsOne() {
        // Given
        var sut = Probability.ZERO;

        // When
        Probability result = sut.complement();

        // Then
        assertThat(result.value()).isEqualTo(1f);
    }

    @Test
    void doubleComplementIsIdentity() {
        // Given
        var sut = Probability.of(0.3f);

        // When
        Probability result = sut.complement().complement();

        // Then
        assertThat(result.value()).isEqualTo(0.3f);
    }

    // ── and / or ────────────────────────────────────────────────────────────

    @Test
    void andMultipliesProbabilities() {
        // Given
        var sut = Probability.of(0.5f);
        var other = Probability.of(0.4f);

        // When
        Probability result = sut.and(other);

        // Then
        assertThat(result.value()).isEqualTo(0.2f);
    }

    @Test
    void andWithZeroIsZero() {
        // Given
        var sut = Probability.of(0.7f);

        // When
        Probability result = sut.and(Probability.ZERO);

        // Then
        assertThat(result.value()).isZero();
    }

    @Test
    void orFollowsInclusionExclusion() {
        // P(A or B) = P(A) + P(B) - P(A) * P(B)  for independent events
        // Given
        var sut = Probability.of(0.5f);
        var other = Probability.of(0.4f);

        // When
        Probability result = sut.or(other);

        // Then
        assertThat(result.value()).isEqualTo(0.7f);
    }

    @Test
    void orWithOneIsOne() {
        // 0.5 chosen for exact float arithmetic: 0.5 + 1 - 0.5 = 1 with no roundoff
        // Given
        var sut = Probability.of(0.5f);

        // When
        Probability result = sut.or(Probability.ONE);

        // Then
        assertThat(result.value()).isEqualTo(1f);
    }

    @Test
    void orClampsToOneAgainstFloatRoundoff() {
        // Two near-one probabilities; arithmetic could exceed 1f by epsilon.
        // Given
        var sut = Probability.of(1f);
        var other = Probability.of(1f);

        // When
        Probability result = sut.or(other);

        // Then
        assertThat(result.value()).isEqualTo(1f);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = Probability.of(0.1f);
        var other = Probability.of(0.9f);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringFormat() {
        // Given
        var sut = Probability.of(0.5f);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Probability(0.5)");
    }

    // ── RefinedFloat ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedFloat() {
        // Given
        RefinedFloat sut = Probability.of(0.42f);

        // When
        float result = sut.value();

        // Then
        assertThat(result).isEqualTo(0.42f);
    }
}
