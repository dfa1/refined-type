# refined-type

Experiments with [Project Valhalla](https://openjdk.org/projects/valhalla/) **value classes** as a refined-type library for Java.

The core question: *can a wrapper type that enforces a constraint at construction time approach the performance of a bare primitive?*
Value classes say yes — no heap allocation, no object header, flat storage in arrays.

Inspired by: [Refined types in practice](https://kwark.github.io/refined-in-practice/#1) — Reference: [original gist](https://gist.github.com/dfa1/f6fdca0513730dc7dc7d6a5d89629709)

Background: [Rethink domain primitives with Valhalla](https://dfa1.github.io/articles/rethink-domain-primitives-with-valhalla)

---

## Why refined types?

A `double` can hold any number. When you pass `lon` where `lat` is expected the compiler stays silent and the bug ships to production.

Refined types encode the constraint in the type itself:

```java
// Before: primitive soup — caller must read the docs to know what's valid
void route(double lat, double lon) { ... }

// After: the type IS the documentation and the guard
void route(Latitude lat, Longitude lon) { ... }
```

Validation runs **once**, at construction. Every subsequent use is guaranteed-valid, with no runtime checks in hot paths. Swapping `lat` and `lon` no longer compiles. Passing `200.0` no longer compiles either.

The same pattern scales to `String` when the constraint is structural. An ISIN (ISO 6166 securities identifier) is a 12-character code: two-letter country prefix, nine-character NSIN, and a Luhn check digit. A raw `String` enforces none of that:

```java
// Before: any string is accepted — validation is the caller's problem
void settle(String isin, String currency) { ... }

settle("US037833100X", "USD");   // bad check digit — compiles, fails at runtime or never
settle("USD", "US0378331005");   // args swapped — compiles, silent wrong-currency settlement
```

```java
// After: constraints live in the types
void settle(Isin isin, CurrencyCode currency) { ... }

// Build from components — check digit computed, cannot be wrong
Isin apple = new Isin(new CountryCode("US"), "037833100"); // → US0378331005

// Domain logic belongs to the type, not scattered across callers
CountryCode country = apple.country();   // → CountryCode("US")

// Swapping args does not compile; CurrencyCode and Isin are distinct types
settle(apple, new CurrencyCode("USD"));
```

---

## Requirements

| Tool  | Version                                  |
|-------|------------------------------------------|
| Java  | 27 EA (Valhalla preview) — **required**  |
| Maven | 3.9+                                     |

```bash
sdk install java 27.ea-open
sdk use java 27.ea-open       # must be active before any mvn command
java -version   # must show openjdk 27-ea
```

---

## Build

```bash
mvn compile        # compile
mvn test           # ~500 tests
```

JMH benchmarks live under `src/test/java/.../bench` — run them from the IDE.

---

## What's in the box

### Marker interfaces

All [F-bounded](https://en.wikipedia.org/wiki/Bounded_quantification#F-bounded_quantification) — `Probability.compareTo(Price)` does not type-check.

| Interface         | Primitive | Default `compareTo`           |
|-------------------|-----------|-------------------------------|
| `RefinedInt<T>`   | `int`     | `Integer.compare`             |
| `RefinedShort<T>` | `short`   | `Short.compare`               |
| `RefinedLong<T>`  | `long`    | `Long.compare`                |
| `RefinedFloat<T>` | `float`   | `Float.compare`               |
| `RefinedDouble<T>`| `double`  | `Double.compare`              |
| `RefinedString<T>`| `String`  | `String.compareTo`            |

Each implementation pins the self-type:

```java
public value class Probability implements RefinedFloat<Probability> { ... }
```

### Unsigned integers (`unsigned` package)

Java's primitives are signed. These value classes give proper unsigned semantics.

#### Why unsigned integers matter

**Domain correctness.** Quantities like a TCP port, a pixel component, a memory address offset, a file size, or a CRC checksum are inherently non-negative. Representing them as `int` or `long` lets `-1` or `-80` compile without complaint. A value class with a [0, 2³²−1] constructor rejects the invalid state at construction time.

**C / C++ / Rust interop.** The Foreign Function & Memory (FFM) API (Java 22+, `java.lang.foreign`) lets Java call native code directly. C uses `uint8_t`, `uint16_t`, `uint32_t`, and `uint64_t` everywhere — network structs, OS syscalls, hardware registers. There is no automatic sign-extension barrier at the JNI / FFM boundary: a C `uint32_t` with value `4_000_000_000` arrives in Java as `-294_967_296` if you naively store it in `int`. Wrapping the raw bits in `UnsignedInt` makes the intent and the correct read-back (`asLong()`) explicit.

```java
// FFM snippet — reading a C uint32_t field from a MemorySegment
int rawBits = segment.get(ValueLayout.JAVA_INT, offset);   // signed bits
UnsignedInt count = UnsignedInt.ofBits(rawBits);           // wrap, no sign error
System.out.println(count.asLong());                        // correct unsigned magnitude
```

**Network protocols and binary formats.** IPv4 addresses, UDP/TCP port numbers, DNS record TTLs, PNG chunk lengths, Ethernet frame types — all unsigned. Parsing binary protocols without an unsigned type forces the developer to remember `& 0xFFFFFFFFL` at every read site. An `UnsignedInt` centralises that contract.

**The JDK gap.** The JDK provides `Integer.toUnsignedLong`, `Integer.compareUnsigned`, and `Integer.divideUnsigned` — unsigned *operations* on signed carriers — but no unsigned *types*. These helpers are easy to forget or misapply. The types in this package wrap the exact same bit-patterns and delegate to those helpers, so the correctness burden shifts from the call site to the constructor.

> **Future:** Valhalla value classes + potential JDK unsigned primitives (discussed in the amber-dev mailing list) could make these wrappers zero-overhead. Until then, they carry a 16–24 byte object header on the heap — acceptable for correctness-critical code, suboptimal for hot loops (use arrays of primitives there).

| Class           | Range            | Notes                                                     |
|-----------------|------------------|-----------------------------------------------------------|
| `UnsignedByte`  | [0, 2⁸ − 1]     | stored as `byte`; constructor takes `int`                 |
| `UnsignedShort` | [0, 2¹⁶ − 1]    | stored as `short`; constructor takes `int`                |
| `UnsignedInt`   | [0, 2³² − 1]     | stored as `int`; `value()` returns raw bits, `asLong()` returns magnitude |
| `UnsignedLong`  | [0, 2⁶⁴ − 1]     | stored as `long`; every bit-pattern is valid              |

Arithmetic suite named after `BigInteger` / `BigDecimal`: `add`, `subtract`, `multiply`, `divide`, `remainder`. Overflow wraps mod 2^N; division uses `Integer.divideUnsigned` / `Long.divideUnsigned`.

```java
var a = new UnsignedInt(3_000_000_000L);
var b = new UnsignedInt(1_000_000_000L);
UnsignedInt sum = a.add(b);                           // 4_000_000_000 — no overflow

UnsignedLong max     = UnsignedLong.MAX;              // 2^64-1, stored as -1L
UnsignedLong wrapped = max.add(new UnsignedLong(1L)); // wraps to ZERO — mod 2^64
```

**Cross-type arithmetic** uses explicit widening — same contract as `Short.toUnsignedInt` in the JDK:

```java
UnsignedShort s = new UnsignedShort(1_000);
UnsignedInt   i = new UnsignedInt(3_000_000_000L);

UnsignedInt result = s.toUnsignedInt().add(i);        // widen first, then add
```

### Half-precision float (`fp` package)

`Float16` — IEEE 754 binary16, stored in **2 bytes**. Range ±65 504, ~3.31 decimal digits.

```java
Float16 a   = Float16.of(1.5f);
Float16 sum = a.add(Float16.of(0.5f));   // Float16(2.0)
Float16.MAX_VALUE.value();               // 65504.0f
Float16.NaN.isNaN();                     // true
```

Arithmetic promotes through `float32` internally — the value-class benefit is **storage density**, not per-operation compute speed on the JVM. Ideal for ML weight arrays, sensor streams, HDR textures.

### Examples (`examples` package)

| Class               | Constraint                                                |
|---------------------|-----------------------------------------------------------|
| `Age`               | integer in `[0, 150]`                                     |
| `AudioSample`       | signed 16-bit PCM sample                                  |
| `CountryCode`       | ISO 3166-1 alpha-2 (two uppercase letters)                |
| `CurrencyCode`      | ISO 4217 currency code (three uppercase letters)          |
| `CusipNumber`       | 9-char CUSIP (US/Canadian securities), → `Isin`           |
| `Distance`          | non-negative metres (with `Unit` enum: M/KM/MILE/NM)      |
| `Email`             | coarse syntactic check                                    |
| `GeoPoint`          | (Latitude, Longitude) pair with haversine `Distance`      |
| `HostName`          | RFC 1123 hostname with SSRF guards                        |
| `Isin`              | ISO 6166 securities identifier (12 chars)                 |
| `Latitude`          | decimal degrees in `[-90, 90]`                            |
| `Longitude`         | decimal degrees in `[-180, 180]`                          |
| `Percentage`        | finite float in `[0, 100]`                                |
| `Port`              | TCP/UDP port in `[0, 65 535]`                             |
| `Price`             | strictly positive, finite float                           |
| `Probability`       | finite float in `[0, 1]`                                  |
| `Size`              | non-negative byte count (with `Unit` enum)                |
| `Slug`              | URL-safe lowercase identifier                             |
| `SwissValorNumber`  | SIX Valoren-Nummer, `[1, 999 999 999]`, → `Isin`          |
| `Velocity`          | non-negative float (m/s)                                  |
| `Volume`            | non-negative, finite float                                |

#### A worked example — securities identifiers

National identifiers compose into international ISINs with check digits computed by the type:

```java
// ABB Ltd — Swiss listing
Isin abb = new SwissValorNumber(1_222_171).toIsin();   // CH0012221716

// Apple Inc. — US listing
Isin apple = new CusipNumber("037833100").toIsin();    // US0378331005

// Or construct directly from country + NSIN; check digit is computed
Isin tesla = new Isin(new CountryCode("US"), "88160R101"); // US88160R1014
```

The wrong-type bug `swissValor.toIsin().equals(cusip)` doesn't compile; both ISINs do compare to each other, but a `SwissValorNumber` cannot be passed where a `CusipNumber` is expected.

---

## Design principles

- **Constructor validates; the type proves it.** Throw `IllegalArgumentException` with a message naming the violated constraint. Never a silent truncation.
- **No nulls.** Constructors reject null inputs; a refined type always holds a valid, non-null value.
- **Fail fast, succeed forever.** Validation runs once at the boundary. Hot paths carry guaranteed-valid values.
- **Explicit over implicit.** Cross-type widening is manual (`toUnsignedInt()`). Jackson integration is opt-in. Caching is opt-in.
- **BigInteger naming for arithmetic.** `add`, `subtract`, `multiply`, `divide`, `remainder` — same names as `BigInteger`/`BigDecimal`.
- **F-bounded markers.** `RefinedFloat<T extends RefinedFloat<T>>` blocks `probability.compareTo(price)` at compile time — same pattern as `java.lang.Enum<E extends Enum<E>>`.

---

## Trade-offs

This pattern is a net positive in the right place, with real costs. Don't apply it project-wide.

### Where it pays

- **Constraint lives in the type signature.** `void route(Latitude, Longitude)` cannot be called with swapped args, cannot accept NaN, cannot accept 200°. The compiler enforces what comments used to beg for.
- **Validation runs once, at the boundary.** Hot paths carry a proof, not raw bytes that "should be valid."
- **Conversions belong to the type** (`SwissValorNumber.toIsin`, `AudioSample.toAmplitude`). Behavior moves toward data; the call site reads as domain prose.
- **Value classes (when Valhalla lands)** push the perf overhead to zero — the wrapper and the bare primitive flatten the same.
- **F-bound stops cross-type compares** like `probability.compareTo(price)` at compile time.

### Costs to take seriously

- **Boilerplate per type.** Constructor, `value()`, `toString`, test class, sometimes `equals`. Eighteen refined types ≈ eighteen near-identical skeletons. Languages with first-class refinement (Scala 3 opaque types, Rust newtype, Kotlin value class) say this in one line.
- **Boundary friction.** Jackson, JPA, JDBC, Bean Validation, MapStruct all expect primitives and `String`. Each refined type needs an adapter — or you pay deserialization tax at every external edge.
- **Generic noise leaks.** `RefinedFloat<T extends RefinedFloat<T>>` is the right shape but ugly in error messages — new contributors stare at it.
- **Sweet spot is narrow.** Big API surfaces, regulated domains (finance, geo, medical), public libraries — huge win. Internal scripts, throwaway endpoints, glue code — boilerplate eats the win.

### Where it pays best

Financial, regulatory, safety-critical code where wrong-type bugs are expensive and inputs cross many layers. The CUSIP/Valor/ISIN chain in this repo is the canonical example: `toIsin()` is impossible to misuse, and passing an Apple CUSIP where a German WKN is expected does not compile.

### Prior art worth checking

| Language | Mechanism                                              |
|----------|--------------------------------------------------------|
| Scala    | `refined`, `iron`, opaque types                        |
| Kotlin   | value classes (`@JvmInline`)                           |
| Rust     | newtype pattern (`pub struct Latitude(f64)`)           |
| Haskell  | `newtype` + smart constructors                         |
| F#       | units of measure (catches lat/long swap at compile time *without* the wrapper noise) |

