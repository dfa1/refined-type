package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedShort;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AudioSampleTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void silenceIsZero() {
        // Given
        var sut = AudioSample.SILENCE;

        // When
        short result = sut.value();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void maxIsShortMax() {
        // Given
        var sut = AudioSample.MAX;

        // When
        short result = sut.value();

        // Then
        assertThat(result).isEqualTo(Short.MAX_VALUE);
    }

    @Test
    void minIsShortMin() {
        // Given
        var sut = AudioSample.MIN;

        // When
        short result = sut.value();

        // Then
        assertThat(result).isEqualTo(Short.MIN_VALUE);
    }

    @Test
    void arbitrarySampleAccepted() {
        // Given
        var sut = new AudioSample((short) 12_345);

        // When
        short result = sut.value();

        // Then
        assertThat(result).isEqualTo((short) 12_345);
    }

    // ── amplitude conversion ────────────────────────────────────────────────

    @Test
    void silenceConvertsToZeroAmplitude() {
        // Given
        var sut = AudioSample.SILENCE;

        // When
        float result = sut.toAmplitude();

        // Then
        assertThat(result).isZero();
    }

    @Test
    void minConvertsToNegativeOne() {
        // Given
        var sut = AudioSample.MIN;

        // When
        float result = sut.toAmplitude();

        // Then
        assertThat(result).isEqualTo(-1.0f);
    }

    @Test
    void maxConvertsToJustUnderOne() {
        // Given
        var sut = AudioSample.MAX;

        // When
        float result = sut.toAmplitude();

        // Then
        assertThat(result).isLessThan(1.0f);
        assertThat(result).isGreaterThan(0.999f);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenSmaller() {
        // Given
        var sut = new AudioSample((short) -100);
        var other = new AudioSample((short) 100);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqualValues() {
        // Given
        var sut = new AudioSample((short) 42);
        var other = new AudioSample((short) 42);

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void compareToReturnsPositiveWhenLarger() {
        // Given
        var sut = AudioSample.MAX;
        var other = AudioSample.MIN;

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isPositive();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesValue() {
        // Given
        var sut = new AudioSample((short) -7_777);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("AudioSample(-7777)");
    }

    // ── RefinedShort ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedShort() {
        // Given
        RefinedShort sut = new AudioSample((short) 1234);

        // When
        short result = sut.value();

        // Then
        assertThat(result).isEqualTo((short) 1234);
    }
}
