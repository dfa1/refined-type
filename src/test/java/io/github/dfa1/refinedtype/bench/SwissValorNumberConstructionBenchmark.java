package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.SwissValorNumber;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/// Measures the cost of constructing collections of 1 000 elements from pre-generated raw ints.
///
/// Each iteration allocates a fresh collection, making allocation the dominant cost.
/// Run with `-prof gc` to observe allocation-rate differences:
///
/// - `bareInt`:         one allocation (int[1000], ~4 KB)
/// - `valueClass`:      one allocation (SwissValorNumber[1000], ~4 KB — value class flattened)
/// - `listOfValueClass`: 1 001+ allocations — ArrayList backing array + 1 000 boxed heap objects;
///                      demonstrates that generic collections erase the flat-layout benefit
/// - `identityClass`:   1 001 allocations (array + 1 000 heap objects × 16 bytes)
///
/// `bareInt` is the zero-overhead baseline; `valueClass` should match it in
/// allocation behaviour, confirming that value-class wrapping costs nothing at rest.
/// `listOfValueClass` vs `valueClass` isolates the generics-boxing penalty.
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
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
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public int[] bareInt() {
        var arr = new int[SIZE];
        System.arraycopy(seed, 0, arr, 0, SIZE);
        return arr;
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public SwissValorNumber[] valueClass() {
        var arr = new SwissValorNumber[SIZE];
        for (int i = 0; i < SIZE; i++) {
            arr[i] = new SwissValorNumber(seed[i]);
        }
        return arr;
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public List<SwissValorNumber> listOfValueClass() {
        var list = new ArrayList<SwissValorNumber>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            list.add(new SwissValorNumber(seed[i]));
        }
        return list;
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public SwissValorNumberIdentity[] identityClass() {
        var arr = new SwissValorNumberIdentity[SIZE];
        for (int i = 0; i < SIZE; i++) {
            arr[i] = new SwissValorNumberIdentity(seed[i]);
        }
        return arr;
    }
}
