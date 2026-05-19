package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedShort;

/// Signed 16-bit linear PCM audio sample: range [-32_768, 32_767].
///
/// The dominant on-disk and on-the-wire format for uncompressed audio
/// (WAV, AIFF, CD-DA, most game-engine sample buffers). One sample per
/// channel per frame; 44_100 frames per second is CD quality.
///
/// Every `short` bit-pattern is a valid PCM sample, so the constructor
/// performs no range check beyond what the primitive itself enforces.
/// The type's value is documentary: a `short` could be a sample, an offset,
/// a length — `AudioSample` says exactly which.
public value class AudioSample implements RefinedShort<AudioSample> {

    /// Digital silence.
    public static final AudioSample SILENCE = new AudioSample((short) 0);
    /// Most-positive sample — full positive peak.
    public static final AudioSample MAX     = new AudioSample(Short.MAX_VALUE);
    /// Most-negative sample — full negative peak.
    public static final AudioSample MIN     = new AudioSample(Short.MIN_VALUE);

    private final short value;

    private AudioSample(short value) {
        this.value = value;
    }

    public static AudioSample of(short value) {
        return new AudioSample(value);
    }

    @Override
    public short value() {
        return value;
    }

    /// Linear amplitude in [-1.0, 1.0]. Divides by 32_768 so that
    /// `MIN.toAmplitude() == -1.0` exactly; `MAX.toAmplitude()` is just
    /// under 1.0, matching the standard PCM asymmetry.
    public float toAmplitude() {
        return value / 32_768f;
    }

    @Override
    public String toString() {
        return "AudioSample(" + value + ")";
    }
}
