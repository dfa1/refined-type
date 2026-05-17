/// Root package — marker interfaces for **refined types**.
///
/// A refined type is a value class that wraps a single primitive (or
/// `String`) and enforces a domain constraint at construction. Once
/// constructed, the value is *guaranteed* to satisfy the constraint;
/// no caller along the hot path needs to revalidate.
///
/// This package defines the six marker interfaces that every refined
/// implementation plugs into:
///
/// - {@link io.github.dfa1.refinedtype.RefinedInt}    — `int` payload
/// - {@link io.github.dfa1.refinedtype.RefinedShort}  — `short` payload
/// - {@link io.github.dfa1.refinedtype.RefinedLong}   — `long` payload
/// - {@link io.github.dfa1.refinedtype.RefinedFloat}  — `float` payload
/// - {@link io.github.dfa1.refinedtype.RefinedDouble} — `double` payload
/// - {@link io.github.dfa1.refinedtype.RefinedString} — `String` payload
///
/// Each marker is F-bounded (`RefinedInt<T extends RefinedInt<T>>`) so
/// the inherited `compareTo` only accepts the same concrete type —
/// `probability.compareTo(price)` does not type-check.
///
/// Concrete implementations live in sibling packages:
///
/// - {@link io.github.dfa1.refinedtype.examples} — domain types
///   (`Age`, `Email`, `Latitude`, ...)
/// - {@link io.github.dfa1.refinedtype.unsigned} — unsigned integer
///   types (`UnsignedByte`, `UnsignedShort`, `UnsignedInt`, `UnsignedLong`)
/// - {@link io.github.dfa1.refinedtype.fp} — alternative
///   floating-point representations (`Float16`)
///
/// **Project goal:** demonstrate that Project Valhalla value classes
/// make this pattern allocation-free and array-flat — i.e. the wrapper
/// approaches the performance of the bare primitive while delivering
/// strictly more type safety.
package io.github.dfa1.refinedtype;
