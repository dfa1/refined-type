package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.SwissValorNumber;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/// Measures linear scan for the maximum valor over 100 000 unique random values.
///
/// Three variants compared in the same file, each forked to a separate JVM to
/// prevent JIT cross-contamination (see `@Fork` per method):
///
/// - `bareInt`       — `int[]`                     (zero-overhead baseline)
/// - `valueClass`    — `SwissValorNumber[]`         (value class, flat layout, ~400 KB)
/// - `identityClass` — `SwissValorNumberIdentity[]` (reference array, ~2 MB)
///
/// At 100 000 elements the flat arrays exceed L2 (~256 KB) but fit in L3; the
/// identity-class array scatters 100 000 heap objects, stressing the prefetcher.
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class SwissValorNumberBenchmark {

    private static final int SIZE = 100_000;

    private int[] bareArray;
    private SwissValorNumber[] valueArray;
    private SwissValorNumberIdentity[] identityArray;

    @Setup
    public void setup() {
        Random rng = new Random(42);
        int[] unique = rng.ints(1, SwissValorNumber.MAX.value() + 1)
                .distinct()
                .limit(SIZE)
                .toArray();

        bareArray = unique;
        valueArray = new SwissValorNumber[SIZE];
        identityArray = new SwissValorNumberIdentity[SIZE];
        for (int i = 0; i < SIZE; i++) {
            valueArray[i]    = SwissValorNumber.of(unique[i]);
            identityArray[i] = new SwissValorNumberIdentity(unique[i]);
        }
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public int bareInt() {
        int max = Integer.MIN_VALUE;
        for (int n : bareArray) {
            if (n > max) max = n;
        }
        return max;
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public int valueClass() {
        int max = Integer.MIN_VALUE;
        for (SwissValorNumber v : valueArray) {
            int n = v.value();
            if (n > max) max = n;
        }
        return max;
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public int identityClass() {
        int max = Integer.MIN_VALUE;
        for (SwissValorNumberIdentity v : identityArray) {
            int n = v.value();
            if (n > max) max = n;
        }
        return max;
    }
}
