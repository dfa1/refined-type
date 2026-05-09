package io.github.dfa1.refinedtypes;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LayoutInspectorTest {

    @Test
    void printLayouts() {
        System.out.println(VM.current().details());

        System.out.println("=== object layout ===");
        System.out.println(ClassLayout.parseClass(PositiveInt.class).toPrintable());
        System.out.println(ClassLayout.parseClass(Integer.class).toPrintable());

        System.out.println("=== array layout ===");
        System.out.println(ClassLayout.parseClass(PositiveInt[].class).toPrintable());
        System.out.println(ClassLayout.parseClass(Integer[].class).toPrintable());
    }

    @Test
    void unsignedInt_array_total_footprint_smaller_than_Integer_array() {
        int n = 100;

        UnsignedInt[] valueArray = new UnsignedInt[n];
        for (int i = 0; i < n; i++) valueArray[i] = new UnsignedInt(i);

        Integer[] boxedArray = new Integer[n];
        for (int i = 256; i < 256 + n; i++) boxedArray[i - 256] = i; // avoid JVM Integer cache

        // UnsignedInt[] stores values inline — array shell includes element data.
        // Integer[] stores references; each Integer object is a separate heap allocation.
        long valueBytes = ClassLayout.parseInstance(valueArray).instanceSize();
        long boxedBytes = ClassLayout.parseInstance(boxedArray).instanceSize()
                + (long) n * ClassLayout.parseClass(Integer.class).instanceSize();

        System.out.printf("UnsignedInt[%d]: %d bytes (inline)%n", n, valueBytes);
        System.out.printf("   Integer[%d]: %d bytes (array shell + %d objects)%n", n, boxedBytes, n);

        assertTrue(valueBytes < boxedBytes,
                "UnsignedInt[%d] (%d bytes) must be smaller than Integer[%d] (%d bytes)"
                        .formatted(n, valueBytes, n, boxedBytes));
    }
}
