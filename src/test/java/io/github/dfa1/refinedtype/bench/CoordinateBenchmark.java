package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.Coordinate;
import io.github.dfa1.refinedtype.examples.Latitude;
import io.github.dfa1.refinedtype.examples.Longitude;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/// Measures the throughput of summing haversine distances from 100 000 random
/// world coordinates to central Rome (41.9028 N, 12.4964 E).
///
/// Two variants:
/// - `valueClass`    — `Coordinate[]` backed by value classes (flat array layout)
/// - `identityClass` — `CoordinateIdentity[]` backed by a plain identity class
///                     (reference array, one heap object per element)
///
/// The haversine computation is identical in both; the only difference is
/// object layout and allocation overhead.
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CoordinateBenchmark {

    private static final int SIZE = 100_000;

    private static final Coordinate ROME_VALUE =
            Coordinate.of(Latitude.of(41.9028), Longitude.of(12.4964));
    private static final CoordinateIdentity ROME_IDENTITY =
            new CoordinateIdentity(41.9028, 12.4964);

    private Coordinate[] valueCoordinates;
    private CoordinateIdentity[] identityCoordinates;

    @Setup
    public void setup() {
        Random rng = new Random(42);
        valueCoordinates = new Coordinate[SIZE];
        identityCoordinates = new CoordinateIdentity[SIZE];
        for (int i = 0; i < SIZE; i++) {
            double lat = -90.0  + rng.nextDouble() * 180.0;
            double lon = -180.0 + rng.nextDouble() * 360.0;
            valueCoordinates[i]    = Coordinate.of(Latitude.of(lat), Longitude.of(lon));
            identityCoordinates[i] = new CoordinateIdentity(lat, lon);
        }
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public double valueClass() {
        double sum = 0.0;
        for (Coordinate c : valueCoordinates) {
            sum += c.distanceTo(ROME_VALUE).value();
        }
        return sum;
    }

    @Benchmark
    @Fork(value = 1, jvmArgsPrepend = "--enable-preview")
    public double identityClass() {
        double sum = 0.0;
        for (CoordinateIdentity c : identityCoordinates) {
            sum += c.distanceTo(ROME_IDENTITY);
        }
        return sum;
    }
}
