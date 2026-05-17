package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.SwissValorNumber;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/// Measures linear scan for the maximum valor over 10 000 unique random
/// {@link SwissValorNumber} values in range [1, 999_999_999].
///
/// Two variants:
/// - `valueClass`    — `SwissValorNumber[]` (value class, flat array layout)
/// - `identityClass` — `SwissValorNumberIdentity[]` (identity class, reference array)
///
/// The algorithm is identical in both; the only difference is object layout.
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = "--enable-preview")
public class SwissValorNumberBenchmark {

    private static final int SIZE = 10_000;

    private SwissValorNumber[] valueArray;
    private SwissValorNumberIdentity[] identityArray;

    @Setup
    public void setup() {
        Random rng = new Random(42);
        int[] unique = rng.ints(1, SwissValorNumber.MAX_VALUE + 1)
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
