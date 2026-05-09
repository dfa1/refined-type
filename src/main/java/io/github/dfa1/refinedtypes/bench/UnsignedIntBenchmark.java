package io.github.dfa1.refinedtypes.bench;

import io.github.dfa1.refinedtypes.unsigned.UnsignedInt;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * Compares UnsignedInt (value class) arithmetic against bare int/long.
 * Unsigned div/rem delegate to Integer.divideUnsigned — cost vs signed div measured here.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = "--enable-preview")
public class UnsignedIntBenchmark {

    private int rawA = 1_000_000_007;
    private int rawB = 31;
    private UnsignedInt unsignedA = new UnsignedInt(1_000_000_007L);
    private UnsignedInt unsignedB = new UnsignedInt(31L);

    @Benchmark
    public int rawAdd() {
        return rawA + rawB;
    }

    @Benchmark
    public UnsignedInt unsignedAdd() {
        return unsignedA.add(unsignedB);
    }

    @Benchmark
    public int rawDivide() {
        return rawA / rawB;
    }

    @Benchmark
    public UnsignedInt unsignedDivide() {
        return unsignedA.divide(unsignedB);
    }

    @Benchmark
    public int rawMultiply() {
        return rawA * rawB;
    }

    @Benchmark
    public UnsignedInt unsignedMultiply() {
        return unsignedA.multiply(unsignedB);
    }
}
