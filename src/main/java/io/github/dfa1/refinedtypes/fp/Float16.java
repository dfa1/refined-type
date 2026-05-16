package io.github.dfa1.refinedtypes.fp;

/// Half-precision IEEE 754 floating-point number (binary16).
///
/// **Format:** 1 sign bit · 5 exponent bits (bias 15) · 10 mantissa bits.
/// Range: ±65504 · ~3.31 decimal digits of precision · stored as 2 bytes.
///
/// **Why use it?**
///
/// - *Memory bandwidth.* A `float16[]` is half the size of `float[]`.
///   At 100 M elements that is 200 MB vs 400 MB — a decisive factor when arrays
///   don't fit in CPU cache or GPU VRAM.
/// - *ML / AI inference.* Neural-network weights and activations are routinely
///   stored in float16 after training. Modern GPUs and NPUs have dedicated float16
///   tensor units that deliver 2–8× the throughput of float32 on the same silicon.
/// - *Sensor and telemetry streams.* Temperature, pressure, gyroscope readings
///   rarely need more than 3 significant digits. float16 encodes them in half the wire
///   bandwidth of float32 with no observable accuracy loss for the application.
/// - *HDR and graphics.* OpenGL / Vulkan / Metal half-float textures and
///   render targets cut VRAM usage by 2× while covering the full visible luminance
///   range.
///
/// **Trade-offs to understand before using it:**
///
/// - Precision is ~3.31 digits — not suitable for financial arithmetic or scientific
///   simulation that requires float32's ~7.2 digits.
/// - Maximum finite value is 65504. Values beyond overflow to ±Infinity.
/// - Arithmetic promotes to float32 internally; the result is then rounded back to
///   float16. There is no hardware float16 ALU on the JVM today, so throughput
///   for scalar operations is not faster than float32.
/// - The benefit is almost exclusively in *storage density and I/O bandwidth*,
///   not in per-operation compute speed on the JVM.
///
/// Backed by {@link Float#floatToFloat16} and {@link Float#float16ToFloat} (Java 20+).
/// Compatible with the Java Vector API's `FLOAT16` element species.
public value class Float16 implements Comparable<Float16> {

    /// Largest finite value: 65504.
    public static final Float16 MAX_VALUE         = new Float16(Float.floatToFloat16(65504f));
    /// Smallest positive non-zero (subnormal): 2^-24 ≈ 5.96e-8.
    public static final Float16 MIN_VALUE         = new Float16(Float.floatToFloat16(0x1.0p-24f));

    public static final Float16 ZERO              = new Float16(Float.floatToFloat16(0f));
    public static final Float16 ONE               = new Float16(Float.floatToFloat16(1f));
    public static final Float16 POSITIVE_INFINITY = new Float16(Float.floatToFloat16(Float.POSITIVE_INFINITY));
    public static final Float16 NEGATIVE_INFINITY = new Float16(Float.floatToFloat16(Float.NEGATIVE_INFINITY));
    public static final Float16 NaN               = new Float16(Float.floatToFloat16(Float.NaN));

    private final short bits;

    /// Construct from raw IEEE 754 half-precision bit pattern. Every bit-pattern is valid.
    public Float16(short bits) {
        this.bits = bits;
    }

    /// Convert float32 to float16, rounding to nearest even. May overflow to ±Infinity.
    public static Float16 of(float value) {
        return new Float16(Float.floatToFloat16(value));
    }

    /// Expand to float32. Exact for all normal and subnormal float16 values.
    public float value() {
        return Float.float16ToFloat(bits);
    }

    /// Raw 16-bit IEEE 754 half-precision bit pattern.
    public short rawBits() {
        return bits;
    }

    public boolean isNaN()      { return Float.isNaN(Float.float16ToFloat(bits)); }
    public boolean isInfinite() { return Float.isInfinite(Float.float16ToFloat(bits)); }
    public boolean isFinite()   { return Float.isFinite(Float.float16ToFloat(bits)); }

    /// Flip sign bit — correct for all bit-patterns including NaN.
    public Float16 negate() {
        return new Float16((short) (bits ^ 0x8000));
    }

    /// Clear sign bit.
    public Float16 abs() {
        return new Float16((short) (bits & 0x7FFF));
    }

    public Float16 add(Float16 other) {
        return of(this.value() + other.value());
    }

    public Float16 subtract(Float16 other) {
        return of(this.value() - other.value());
    }

    public Float16 multiply(Float16 other) {
        return of(this.value() * other.value());
    }

    /// IEEE 754: x / 0 yields ±Infinity, not ArithmeticException.
    public Float16 divide(Float16 other) {
        return of(this.value() / other.value());
    }

    @Override
    public int compareTo(Float16 that) {
        return Float.compare(this.value(), that.value());
    }

    @Override
    public String toString() {
        return "Float16(" + Float.float16ToFloat(bits) + ")";
    }
}
