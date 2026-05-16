/// Unsigned integer refined types.
///
/// Java's primitive integer types are all signed. This package supplies
/// value classes that give the four unsigned-integer widths proper
/// unsigned semantics — range, arithmetic, comparison, and string form.
///
/// | Class                                                          | Range            | Storage          |
/// |----------------------------------------------------------------|------------------|------------------|
/// | {@link io.github.dfa1.refinedtypes.unsigned.UnsignedByte}      | `[0, 255]`       | raw `byte` bits  |
/// | {@link io.github.dfa1.refinedtypes.unsigned.UnsignedShort}     | `[0, 65_535]`    | raw `short` bits |
/// | {@link io.github.dfa1.refinedtypes.unsigned.UnsignedInt}       | `[0, 2³² − 1]`   | raw `int` bits   |
/// | {@link io.github.dfa1.refinedtypes.unsigned.UnsignedLong}      | `[0, 2⁶⁴ − 1]`   | raw `long` bits  |
///
/// Arithmetic methods (`add`, `subtract`, `multiply`, `divide`,
/// `remainder`) are named after the {@link java.math.BigInteger} /
/// {@link java.math.BigDecimal} convention and follow unsigned-modulo
/// semantics — overflow wraps mod 2^N, division uses
/// {@link Integer#divideUnsigned} / {@link Long#divideUnsigned}.
///
/// Cross-width arithmetic is *not* offered as overloads. Callers widen
/// explicitly (`UnsignedShort.toUnsignedInt()`) and then operate at the
/// chosen precision — same contract as {@link Short#toUnsignedInt} in
/// the JDK.
package io.github.dfa1.refinedtypes.unsigned;
