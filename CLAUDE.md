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

Use the Maven wrapper — it pins the exact Maven version and does not require a system install:

```bash
./mvnw compile          # compile
./mvnw test             # unit tests
./mvnw verify -Pbenchmark   # JMH benchmarks (profile not yet wired)
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

## Coding conventions

- Value class implementations go in `refinedtypes` package alongside the interfaces
- Constructor validates; throws `IllegalArgumentException` with message naming the violated constraint
- No nulls — value classes cannot be null
- Benchmark classes in `src/test/java` under the `bench/` package, named `*Benchmark` (JMH is `test`-scoped)

## Style

- **American English** in all code, comments, and docs (e.g. `canceled` not `cancelled`, `color` not `colour`, `behavior` not `behaviour`)
- **Javadoc in Markdown style** — use the JEP 467 `///` Markdown doc-comment form instead of the legacy `/** ... */` HTML form. Inline HTML tags (`<p>`, `<ul>`, `<em>`, `<b>`) are replaced by their Markdown equivalents.

## Test conventions

- Test method names: camelCase, no underscores
- All tests structured with `// Given`, `// When`, `// Then` comments
- Variable named `sut` for the system under test
- Variable named `result` for the return value of the method under test in `// When`
- Use AssertJ (`assertThat`) — never JUnit `assertEquals` / `assertThrows` etc.

```java
@Test
void validEmailAccepted() {
    // Given
    var sut = new Email("user@example.com");

    // When
    String result = sut.value();

    // Then
    assertThat(result).isEqualTo("user@example.com");
}
```

## What to build next

1. JMH benchmark: allocation rate + throughput vs bare `int`
2. Expand: more domain examples
3. Compare identity-class vs value-class variants in same benchmark
