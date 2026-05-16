package io.github.dfa1.refinedtypes.bench;

import io.github.dfa1.refinedtypes.examples.PositiveInt;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/// Compares PositiveInt (value class) against bare int.
/// Goal: show that value-class overhead is negligible in arithmetic hot paths.
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = "--enable-preview")
public class PositiveIntBenchmark {

    private int rawA = 7;
    private int rawB = 3;
    private PositiveInt valueA = new PositiveInt(7);
    private PositiveInt valueB = new PositiveInt(3);

    @Benchmark
    public int rawAdd() {
        return rawA + rawB;
    }

    @Benchmark
    public int rawCompare() {
        return Integer.compare(rawA, rawB);
    }

    @Benchmark
    public PositiveInt valueCreate() {
        return new PositiveInt(rawA);
    }

    @Benchmark
    public int valueGet() {
        return valueA.value();
    }

    @Benchmark
    public int valueCompare() {
        return valueA.compareTo(valueB);
    }
}
