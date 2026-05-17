package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.SwissValorNumber;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/// Measures linear scan for the maximum valor over 100 000 unique random
/// {@link SwissValorNumber} values in range [1, 999_999_999].
///
/// Two variants:
/// - `valueClass`    — `SwissValorNumber[]` (value class, flat array layout, ~400 KB)
/// - `identityClass` — `SwissValorNumberIdentity[]` (identity class, reference array, ~2 MB)
///
/// At 100 000 elements the value-class array exceeds L2 (~256 KB) but fits
/// in L3; the identity-class array scatters 100 000 objects across the heap,
/// stressing the prefetcher and amplifying the layout difference.
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = "--enable-preview")
public class SwissValorNumberBenchmark {

    private static final int SIZE = 100_000;

    private SwissValorNumber[] valueArray;
    private SwissValorNumberIdentity[] identityArray;

    @Setup
    public void setup() {
        Random rng = new Random(42);
        int[] unique = rng.ints(1, SwissValorNumber.MAX.value() + 1)
                .distinct()
                .limit(SIZE)
                .toArray();

        valueArray = new SwissValorNumber[SIZE];
        identityArray = new SwissValorNumberIdentity[SIZE];
        for (int i = 0; i < SIZE; i++) {
            valueArray[i]    = new SwissValorNumber(unique[i]);
            identityArray[i] = new SwissValorNumberIdentity(unique[i]);
        }
    }

    @Benchmark
    public int valueClass() {
        int max = Integer.MIN_VALUE;
        for (SwissValorNumber v : valueArray) {
            int n = v.value();
            if (n > max) max = n;
        }
        return max;
    }

    @Benchmark
    public int identityClass() {
        int max = Integer.MIN_VALUE;
        for (SwissValorNumberIdentity v : identityArray) {
            int n = v.value();
            if (n > max) max = n;
        }
        return max;
    }
}
