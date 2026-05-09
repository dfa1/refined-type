# CLAUDE.md

## Project

Benchmarking Valhalla value classes as refined-type implementation. Goal: prove value-class wrappers approach raw-primitive perf.

## Stack

- Java 27 EA (Valhalla preview) — **required**, no fallback to older JDK
- Maven 3.9+
- JMH for microbenchmarks (to be wired in)

## JDK setup

```bash
sdk install java 27.ea-open
sdk use java 27.ea-open
```

Verify: `java -version` must show `openjdk 27-ea`.

## Key flags

Valhalla preview features need explicit enablement:

```xml
<compilerArgs>
  <arg>--enable-preview</arg>
</compilerArgs>
```

And at runtime: `java --enable-preview`.

## Source layout

```
src/main/java/io/github/dfa1/refinedtypes/
  RefinedInt.java      # marker interface
  RefinedFloat.java    # marker interface
  # TODO: value class implementations + JMH benchmarks
```

## Build commands

```bash
mvn compile          # compile
mvn test             # unit tests
mvn verify -Pbenchmark   # JMH benchmarks (profile not yet wired)
```

## Coding conventions

- Value class implementations go in `refinedtypes` package alongside the interfaces
- Constructor validates; throws `IllegalArgumentException` with message naming the violated constraint
- No nulls — value classes cannot be null
- Benchmark classes in `src/main/java` (JMH convention), named `*Benchmark`

## What to build next

1. `value class PositiveInt implements RefinedInt` — simplest case
2. JMH benchmark: allocation rate + throughput vs bare `int`
3. Expand: `Age`, `Percentage`, `NonNegativeFloat`
4. Compare identity-class vs value-class variants in same benchmark
