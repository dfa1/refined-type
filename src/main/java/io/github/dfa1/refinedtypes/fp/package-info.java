/// Alternative floating-point representations.
///
/// The JVM exposes `float` (32-bit IEEE 754 binary32) and `double`
/// (64-bit binary64) as primitives. This package adds value-class
/// wrappers for other IEEE 754 widths that the JVM supports through
/// conversion intrinsics but not as first-class primitives.
///
/// | Class                                                      | Format                | Storage         |
/// |------------------------------------------------------------|-----------------------|-----------------|
/// | {@link io.github.dfa1.refinedtypes.fp.Float16}             | IEEE 754 binary16     | 2 bytes (`short`) |
///
/// `Float16` is built on {@link Float#floatToFloat16} and
/// {@link Float#float16ToFloat} (Java 20+). Its benefit is **storage
/// density** — half the size of `float32` for arrays of weights,
/// telemetry samples, HDR pixels — not per-scalar compute speed, since
/// arithmetic still promotes through `float32` on the JVM.
package io.github.dfa1.refinedtypes.fp;
