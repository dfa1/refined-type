package io.github.dfa1.refinedtypes.bench;

import io.github.dfa1.refinedtypes.unsigned.UnsignedInt;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Compares array traversal between UnsignedInt[] (flat value-class storage)
 * and Integer[] (pointer array + heap objects).
 * The 6.8× size difference should translate to measurable cache-locality wins.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = "--enable-preview")
public class ArrayTraversalBenchmark {

    @Param({"1000", "100000"})
    int size;

    private UnsignedInt[] valueArray;
    private Integer[]     boxedArray;

    @Setup
    public void setup() {
        valueArray = new UnsignedInt[size];
        boxedArray = new Integer[size];
        for (int i = 0; i < size; i++) {
            valueArray[i] = new UnsignedInt(i);
            boxedArray[i] = i;
        }
    }

    @Benchmark
    public long sumValueArray() {
        long sum = 0;
        for (UnsignedInt v : valueArray) {
            sum += v.value();
        }
        return sum;
    }

    @Benchmark
    public long sumBoxedArray() {
        long sum = 0;
        for (Integer v : boxedArray) {
            sum += v;
        }
        return sum;
    }
}
