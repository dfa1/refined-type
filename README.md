# refined-type

Benchmark exploring [Project Valhalla](https://openjdk.org/projects/valhalla/) value classes as a zero-cost abstraction for refined types in Java.

## Motivation

Refined types wrap primitives with compile-time-named validation constraints:

```java
// Bad: raw int leaks everywhere, no invariants enforced
void setAge(int age) { ... }

// Good: type carries its invariant
void setAge(Age age) { ... }
```

Classic Java implementation requires heap allocation per instance. Valhalla **value classes** eliminate object identity, enabling flat/inline layout — so `Age` can be as cheap as a bare `int` on the JVM.

Reference: [original gist](https://gist.github.com/dfa1/f6fdca0513730dc7dc7d6a5d89629709)

## Hypothesis

Value-class refined types approach the performance of raw primitives, closing the gap that makes refined types impractical in hot paths today.

## Design

- `RefinedInt` / `RefinedFloat` — marker interfaces for refined primitive wrappers
- Implementations declared as `value class` (Valhalla preview feature)
- Validation in constructor; throws `IllegalArgumentException` on violation

## Requirements

- JDK 27 EA (Valhalla preview features enabled)
- Maven 3.9+

### Install JDK 27 EA via SDKMAN

```bash
sdk install java 27.ea-open   # or check: sdk list java | grep 27
sdk use java 27.ea-open
java -version                 # verify: openjdk 27-ea
```

## Build

```bash
mvn compile
mvn test
```

## Benchmarks

```bash
mvn verify -Pbenchmark
```

Results land in `target/benchmark-results/`.
