package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.Coordinate;
import io.github.dfa1.refinedtype.examples.Latitude;
import io.github.dfa1.refinedtype.examples.Longitude;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;

class CoordinateLayoutTest {

    @Test
    void printLayouts() {
        // NOTE: instanceSize() from JOL shows the *boxed/heap* form of a value class —
        // what the JVM allocates when the value escapes to Object. It includes the full
        // object header (12 bytes) plus alignment padding and is NOT what gets stored in
        // a typed Coordinate[] array. In a flat typed array only the payload (2 × 8 bytes
        // for the two nested double value classes) is stored per element — header-free.
        System.out.println("=== value class Coordinate (boxed/heap form) ===");
        System.out.println(ClassLayout.parseClass(Coordinate.class).toPrintable());

        System.out.println("=== identity class CoordinateIdentity ===");
        System.out.println(ClassLayout.parseClass(CoordinateIdentity.class).toPrintable());

        System.out.printf("Coordinate         boxed heap size : %d bytes (header + nested value fields + padding)%n",
                ClassLayout.parseClass(Coordinate.class).instanceSize());
        System.out.printf("CoordinateIdentity heap size       : %d bytes%n",
                ClassLayout.parseClass(CoordinateIdentity.class).instanceSize());
    }

    @Test
    void printArrayFootprintFor100K() {
        int n = 100_000;
        Coordinate[] valueArr = new Coordinate[n];
        CoordinateIdentity[] identityArr = new CoordinateIdentity[n];
        for (int i = 0; i < n; i++) {
            double lat  = -90.0  + i * (180.0 / n);
            double lon  = -180.0 + i * (360.0 / n);
            valueArr[i]    = Coordinate.of(Latitude.of(lat), Longitude.of(lon));
            identityArr[i] = new CoordinateIdentity(lat, lon);
        }

        long valueTotal    = ClassLayout.parseInstance(valueArr).instanceSize();
        long identityShell = ClassLayout.parseInstance(identityArr).instanceSize();
        long identityObj   = ClassLayout.parseClass(CoordinateIdentity.class).instanceSize();
        long identityTotal = identityShell + (long) n * identityObj;
        long bytesPerValueElement = (valueTotal - 16) / n;
        // bytesPerValueElement should be 16 (2 × double payload) for a truly flat array.
        // If JOL reports 4 bytes/element it is misreading the array as a reference array
        // (compressed 4-byte OOPs) — a known limitation of JOL 0.17 with Valhalla EA.

        System.out.printf("Coordinate[%d]         total: %,d bytes  (%d bytes/element as reported by JOL)%n",
                n, valueTotal, bytesPerValueElement);
        System.out.printf("CoordinateIdentity[%d] total: %,d bytes  (%d-byte refs + %,d objects × %d bytes)%n",
                n, identityTotal, 4, n, identityObj);
        System.out.printf("Memory ratio (identity/value): %.2fx%n",
                (double) identityTotal / valueTotal);
    }
}
