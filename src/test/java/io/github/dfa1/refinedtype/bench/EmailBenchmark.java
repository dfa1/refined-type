package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.Email;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/// Measures the cost of constructing 1_000 {@link Email} wrappers from
/// pre-generated valid email strings, revealing the overhead of
/// {@code RefinedString} wrapping relative to raw {@code String} storage.
///
/// Unlike numeric value classes, {@code Email} holds a {@code String} reference.
/// The value-class wrapper flattens (no extra heap object per element), but the
/// underlying {@code String} allocation cannot be eliminated. The three variants
/// isolate what each layer costs:
///
/// - `rawString`:      copy pre-existing String refs; no validation
/// - `valueClass`:     Email[] with validation; wrapper flattens — cost is validation only
/// - `identityClass`:  EmailIdentity[] with validation; adds one heap object per element
///
/// Expected outcome: `valueClass` sits between `rawString` (fastest) and
/// `identityClass` (most allocations), with the gap to `rawString` reflecting
/// pure validation overhead (indexOf, substring, contains).
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class EmailBenchmark {

    private static final int SIZE = 1_000;

    private String[] seed;

    @Setup
    public void setup() {
        seed = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            seed[i] = "user" + i + "@bench.io";
        }
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public String[] rawString() {
        var arr = new String[SIZE];
        System.arraycopy(seed, 0, arr, 0, SIZE);
        return arr;
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public Email[] valueClass() {
        var arr = new Email[SIZE];
        for (int i = 0; i < SIZE; i++) {
            arr[i] = Email.of(seed[i]);
        }
        return arr;
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public EmailIdentity[] identityClass() {
        var arr = new EmailIdentity[SIZE];
        for (int i = 0; i < SIZE; i++) {
            arr[i] = new EmailIdentity(seed[i]);
        }
        return arr;
    }
}
