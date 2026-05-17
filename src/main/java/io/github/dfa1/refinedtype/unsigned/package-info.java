/// Unsigned integer refined types.
///
/// Java's primitive integer types are all signed. This package supplies
/// value classes that give the four unsigned-integer widths proper
/// unsigned semantics — range, arithmetic, comparison, and string form.
///
/// | Class                                                          | Range            | Storage          |
/// |----------------------------------------------------------------|------------------|------------------|
/// | {@link io.github.dfa1.refinedtype.unsigned.UnsignedByte}      | `[0, 255]`       | raw `byte` bits  |
/// | {@link io.github.dfa1.refinedtype.unsigned.UnsignedShort}     | `[0, 65_535]`    | raw `short` bits |
/// | {@link io.github.dfa1.refinedtype.unsigned.UnsignedInt}       | `[0, 2³² − 1]`   | raw `int` bits   |
/// | {@link io.github.dfa1.refinedtype.unsigned.UnsignedLong}      | `[0, 2⁶⁴ − 1]`   | raw `long` bits  |
///
/// ## Why unsigned types?
///
/// **Domain correctness.** Quantities such as TCP ports, pixel components,
/// memory address offsets, file sizes, and CRC checksums are inherently
/// non-negative. Storing them in `int` or `long` lets `-1` or `-80` compile
/// silently; these constructors reject the invalid state at construction time.
///
/// **C / C++ / Rust interop via FFM.** The Foreign Function & Memory API
/// (Java 22+, `java.lang.foreign`) maps C types like `uint32_t` directly
/// onto `int` bits. A C `uint32_t` value of `4_000_000_000` arrives in Java
/// as `-294_967_296` if stored naively in an `int`. Wrapping the raw bits in
/// `UnsignedInt` makes the correct unsigned read-back (`asLong()`) explicit
/// and impossible to forget.
///
/// **Network protocols and binary formats.** IPv4 addresses, UDP/TCP port
/// numbers, DNS TTLs, PNG chunk lengths, and Ethernet frame types are all
/// unsigned. Without unsigned types the developer must remember
/// `& 0xFFFFFFFFL` at every read site; these classes centralise that contract.
///
/// **The JDK gap.** The JDK provides unsigned *operations*
/// ({@link Integer#toUnsignedLong}, {@link Integer#compareUnsigned},
/// {@link Integer#divideUnsigned}) on signed carriers, but no unsigned
/// *types*. These helpers are easy to forget or misapply; the classes here
/// wrap the same bit-patterns and delegate to them so the correctness burden
/// shifts to the constructor.
///
/// ## Arithmetic
///
/// Methods (`add`, `subtract`, `multiply`, `divide`, `remainder`) follow the
/// {@link java.math.BigInteger} / {@link java.math.BigDecimal} naming
/// convention. Overflow wraps mod 2^N; division uses
/// {@link Integer#divideUnsigned} / {@link Long#divideUnsigned}.
///
/// Cross-width arithmetic is *not* offered as overloads. Callers widen
/// explicitly (`UnsignedShort.toUnsignedInt()`) and then operate at the
/// chosen precision — same contract as {@link Short#toUnsignedInt} in the JDK.
package io.github.dfa1.refinedtype.unsigned;
