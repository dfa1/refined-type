# refined-type

Experiments with [Project Valhalla](https://openjdk.org/projects/valhalla/) **value classes** as a refined-type library for Java.

The core question: *can a wrapper type that enforces a constraint at construction time approach the performance of a bare primitive?*  
Value classes say yes — no heap allocation, no object header, flat storage in arrays.

Inspired by: [Refined types in practice](https://kwark.github.io/refined-in-practice/#1) — Reference: [original gist](https://gist.github.com/dfa1/f6fdca0513730dc7dc7d6a5d89629709)

---

## Why refined types?

A `String` can hold anything. An `int` can be negative. When you pass `userId` where `orderId` is expected the compiler stays silent and the bug ships.

Refined types solve this by encoding the constraint in the type itself:

```java
// Before: primitive soup — caller must read the docs to know what's valid
void ship(int userId, int orderId, String email) { ... }

// After: the type IS the documentation and the guard
void ship(PositiveInt userId, PositiveInt orderId, Email email) { ... }
```

Validation runs **once**, at construction. Every subsequent use is guaranteed-valid, with no runtime checks in hot paths.

---

## Why value classes?

Traditional wrapper types pay an object-per-value tax:

| | Heap alloc | Object header | Array layout |
|---|---|---|---|
| `Integer[]` (100 elements) | 100 objects | 16 bytes each | pointer array → scattered objects |
| `PositiveInt[]` (100 elements, value class) | **0** | **0** | **flat** — primitives inline |

Measured with JOL on this JDK:

```
UnsignedInt[100]:   416 bytes  (flat inline storage)
   Integer[100]:  2816 bytes  (array shell + 100 heap objects)
```

**6.8× smaller.** Better cache locality. Zero GC pressure.

---

## Requirements

| Tool | Version |
|------|---------|
| Java | 27 EA (Valhalla preview) — **required** |
| Maven | 3.9+ |

```bash
sdk install java 27.ea-open
sdk use java 27.ea-open
java -version   # must show openjdk 27-ea
```

---

## Build

```bash
mvn compile          # compile
mvn test             # 189 tests
```

---

## What's in the box

### Marker interfaces

| Interface | Primitive | Key method |
|-----------|-----------|------------|
| `RefinedInt` | `int` | `int value()` |
| `RefinedFloat` | `float` | `float value()` |
| `RefinedString` | `String` | `String value()` |

All extend `Comparable` with a sensible default `compareTo`.

### Unsigned integers (`unsigned` package)

Java's primitives are signed. These value classes give you proper unsigned semantics.

| Class | Range | Notes |
|-------|-------|-------|
| `UnsignedShort` | [0, 65 535] | stored as `short`; constructor takes `int` and validates |
| `UnsignedInt` | [0, 4 294 967 295] | stored as `int`; constructor takes `long` and validates |
| `UnsignedLong` | [0, 2⁶⁴ − 1] | stored as `long`; **no validation** — every `long` bit-pattern is valid |

All three implement `Comparable` with unsigned ordering and carry the full arithmetic suite
named after `BigInteger`/`BigDecimal`: `add`, `subtract`, `multiply`, `divide`, `remainder`.

```java
var a = new UnsignedInt(3_000_000_000L);
var b = new UnsignedInt(1_000_000_000L);
UnsignedInt sum = a.add(b);                           // 4_000_000_000 — no overflow

UnsignedLong max     = UnsignedLong.MAX;              // 2^64-1, stored as -1L
UnsignedLong wrapped = max.add(new UnsignedLong(1L)); // wraps to ZERO — mod 2^64
```

**Cross-type arithmetic** uses explicit widening — same contract as `Short.toUnsignedInt` in the JDK.
Overloads for every combination would require O(types² × ops) methods; explicit widening keeps the API flat:

```java
UnsignedShort s = new UnsignedShort(1_000);
UnsignedInt   i = new UnsignedInt(3_000_000_000L);

UnsignedInt result = s.toUnsignedInt().add(i);        // widen first, then add
```

### Half-precision float (`fp` package)

`Float16` — IEEE 754 binary16: 1 sign · 5 exponent · 10 mantissa bits.
Range ±65 504, ~3.31 decimal digits, stored in **2 bytes**.

```java
Float16 a   = Float16.of(1.5f);
Float16 b   = Float16.of(0.5f);
Float16 sum = a.add(b);              // Float16(2.0)

Float16.MAX_VALUE.value();           // 65504.0f
Float16.NaN.isNaN();                 // true
Float16.of(-7.5f).abs();             // Float16(7.5)
```

Arithmetic promotes through `float32` internally — the value-class benefit is **storage density**,
not per-operation compute speed on the JVM. Ideal for ML weight arrays, sensor streams, HDR textures.

### Examples (`examples` package)

Ready-to-use refined types demonstrating the pattern:

| Class | Constraint |
|-------|-----------|
| `PositiveInt` | `value > 0` |
| `Email` | one `@`, non-empty local part, domain contains `.` |
| `Country` | ISO 3166-1 alpha-2, uppercase |
| `Isin` | 12-char `[A-Z]{2}[A-Z0-9]{9}[0-9]` |
| `Velocity` | `value >= 0` (non-negative float) |

### Jackson integration (`jackson` package)

`RefinedTypesModule` — annotation-free Jackson serialization for any refined type.

- Uses `MethodHandles.privateLookupIn` instead of `setAccessible` — works with value classes, JIT-inlinable
- Single-value types serialize as their unwrapped primitive
- Register once, works for all refined types:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new RefinedTypesModule());

String json  = mapper.writeValueAsString(new Email("user@example.com")); // "user@example.com"
Email  email = mapper.readValue(json, Email.class);
```

### Pluggable cache (`cache` package)

`RefinedTypeCache` — optional interning for low-cardinality types (country codes, statuses, etc.).

```java
private static final RefinedTypeCache CACHE = new ConcurrentMapCache();

// In a static factory method:
public static Country of(String code) {
    return CACHE.get(Country.class, code, () -> new Country(code));
}
```

`RefinedTypeCache.NO_OP` is a zero-overhead pass-through for types where caching is not needed.

---

## Design principles

- **Constructor validates; the type proves it.** Throw `IllegalArgumentException` with a message naming the violated constraint. Never a silent truncation.
- **No nulls.** Value classes cannot be null — the compiler enforces it.
- **Fail fast, succeed forever.** Validation runs once at the boundary. Hot paths carry guaranteed-valid values.
- **Explicit over implicit.** Cross-type widening is manual (`toUnsignedInt()`). Jackson integration is opt-in. Caching is opt-in.
- **BigInteger naming for arithmetic.** `add`, `subtract`, `multiply`, `divide`, `remainder` — familiar to any Java developer.
- **No reflection.** `MethodHandles.privateLookupIn` for constructor access — compatible with value classes and the module system.
