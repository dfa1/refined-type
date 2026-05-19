# CLAUDE.md

## Project

Benchmarking Valhalla value classes as refined-type implementation. Goal: prove value-class wrappers approach raw-primitive perf.

## Stack

- Java 27 EA (Valhalla preview) — **required**, no fallback to older JDK
- Maven 3.9+
- JMH for microbenchmarks

## JDK setup

SDKMAN is **not installed**. Valhalla EA JDK lives at:

```
/Users/dfa/Library/Java/JavaVirtualMachines/valhalla-ea-27-jep401ea3+1-1/Contents/Home
```

Always prepend `JAVA_HOME=<path above>` to Maven invocations — Homebrew `mvn` defaults to JDK 25 and rejects `--release 27`.

Verify: `java -version` must show `openjdk 27-jep401ea3`.

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
src/main/java/io/github/dfa1/refinedtype/
  RefinedInt.java          # marker interface
  RefinedFloat.java        # marker interface
  examples/                # value class implementations
  unsigned/                # unsigned integer types

src/test/java/io/github/dfa1/refinedtype/
  bench/                   # JMH benchmarks + identity-class mirrors + JOL layout tests
  examples/                # unit tests per example class
  unsigned/                # unit tests for unsigned types
```

## Build commands

Use the Maven wrapper — it pins the exact Maven version and does not require a system install:

```bash
JAVA_HOME=<valhalla-path> ./mvnw compile    # compile
JAVA_HOME=<valhalla-path> ./mvnw test       # unit tests
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

## Running benchmarks

Use `bench.sh` — sets `JAVA_HOME`, compiles, and runs JMH. No manual classpath needed.

```bash
./bench.sh                              # all benchmarks
./bench.sh SwissValorNumber             # filter by name (regex)
./bench.sh --layout SwissValorNumber    # JOL memory layout, then JMH
./bench.sh --layout-only               # layout inspection only
./bench.sh --gc SwissValorNumber        # add GC alloc-rate profiler (-prof gc)
```

`--layout` prints JOL footprint tables before JMH results so both are visible together.

Each `@Benchmark` method uses its own `@Fork` — value/identity/bare-int variants run in separate JVMs to prevent JIT cross-contamination. `bench.sh` does not need to enforce order; JMH handles fork isolation.

To filter from Maven directly (bypasses `bench.sh`):

```bash
JAVA_HOME=<valhalla-path> ./mvnw verify -Pbenchmark -Dbenchmark.filter=SwissValorNumber
```

## Coding conventions

- Value class implementations go in `refinedtype` package alongside the interfaces
- Constructor validates; throws `IllegalArgumentException` with message naming the violated constraint
- No nulls — value classes cannot be null
- Benchmark classes in `src/test/java` under the `bench/` package, named `*Benchmark` (JMH is `test`-scoped)
- **Length before regex** — always check `String` length (or reject obviously-wrong sizes) before calling `Pattern.matcher(...).matches()`. A malicious input can cause catastrophic backtracking (ReDoS) on unbounded input even with simple-looking patterns; a cheap length guard caps the regex input size and makes evaluation linear in the bound.

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

1. Expand: more domain examples
2. Add `--gc` profiler results to benchmark docs once stable numbers are collected
