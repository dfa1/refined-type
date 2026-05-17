package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.Coordinate;
import io.github.dfa1.refinedtype.examples.Latitude;
import io.github.dfa1.refinedtype.examples.Longitude;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;

class CoordinateLayoutTest {

    @Test
    void printLayouts() {
        System.out.println("=== value class Coordinate ===");
        System.out.println(ClassLayout.parseClass(Coordinate.class).toPrintable());

        System.out.println("=== identity class CoordinateIdentity ===");
        System.out.println(ClassLayout.parseClass(CoordinateIdentity.class).toPrintable());

        System.out.printf("Coordinate        instance size: %d bytes%n",
                ClassLayout.parseClass(Coordinate.class).instanceSize());
        System.out.printf("CoordinateIdentity instance size: %d bytes%n",
                ClassLayout.parseClass(CoordinateIdentity.class).instanceSize());

        System.out.println();
        System.out.println("=== Coordinate[] (value class array — flat layout) ===");
        System.out.println(ClassLayout.parseClass(Coordinate[].class).toPrintable());

        System.out.println("=== CoordinateIdentity[] (identity array — reference layout) ===");
        System.out.println(ClassLayout.parseClass(CoordinateIdentity[].class).toPrintable());
    }

    @Test
    void printArrayFootprintFor100K() {
        int n = 100_000;
        Coordinate[] valueArr = new Coordinate[n];
        CoordinateIdentity[] identityArr = new CoordinateIdentity[n];
        for (int i = 0; i < n; i++) {
            double lat  = -90.0  + i * (180.0 / n);
            double lon  = -180.0 + i * (360.0 / n);
            valueArr[i]    = new Coordinate(new Latitude(lat), new Longitude(lon));
            identityArr[i] = new CoordinateIdentity(lat, lon);
        }

        long valueArrayShell    = ClassLayout.parseInstance(valueArr).instanceSize();
        long identityArrayShell = ClassLayout.parseInstance(identityArr).instanceSize();
        long identityObjSize    = ClassLayout.parseClass(CoordinateIdentity.class).instanceSize();
        long identityTotal      = identityArrayShell + (long) n * identityObjSize;

        System.out.printf("Coordinate[%d]         array shell: %,d bytes (elements stored inline)%n",
                n, valueArrayShell);
        System.out.printf("CoordinateIdentity[%d] array shell: %,d bytes + %,d objects × %d bytes = %,d bytes total%n",
                n, identityArrayShell, n, identityObjSize, identityTotal);
        System.out.printf("Memory ratio (identity/value): %.2fx%n",
                (double) identityTotal / valueArrayShell);
    }
}
