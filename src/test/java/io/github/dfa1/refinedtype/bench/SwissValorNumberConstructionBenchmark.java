package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.SwissValorNumber;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/// Measures the cost of constructing an array of 1 000 {@link SwissValorNumber}
/// values from pre-generated raw `int` seeds.
///
/// Unlike the scan benchmark (which pre-allocates arrays in `@Setup`), each
/// iteration here allocates a fresh array — making allocation the dominant work.
/// Run with `-prof gc` to see the allocation-rate difference:
///
/// - `valueClass`: one heap allocation (the array itself, 4 bytes × 1 000)
/// - `identityClass`: 1 001 heap allocations (array + 1 000 individual objects × 16 bytes)
///
/// The 5× allocation ratio mirrors the JOL footprint numbers and translates
/// directly into reduced GC CPU overhead for the value-class variant.
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = "--enable-preview")
public class SwissValorNumberConstructionBenchmark {

    private static final int SIZE = 1_000;

    private int[] seed;

    @Setup
    public void setup() {
        Random rng = new Random(42);
        seed = rng.ints(1, SwissValorNumber.MAX.value() + 1)
                .limit(SIZE)
                .toArray();
    }

    @Benchmark
    public SwissValorNumber[] valueClass() {
        var arr = new SwissValorNumber[SIZE];
        for (int i = 0; i < SIZE; i++) {
            arr[i] = new SwissValorNumber(seed[i]);
        }
        return arr;
    }

    @Benchmark
    public SwissValorNumberIdentity[] identityClass() {
        var arr = new SwissValorNumberIdentity[SIZE];
        for (int i = 0; i < SIZE; i++) {
            arr[i] = new SwissValorNumberIdentity(seed[i]);
        }
        return arr;
    }
}
