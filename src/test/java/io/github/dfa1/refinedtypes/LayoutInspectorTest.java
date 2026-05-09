package io.github.dfa1.refinedtypes;

import io.github.dfa1.refinedtypes.unsigned.UnsignedInt;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

import static org.assertj.core.api.Assertions.assertThat;

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
    void unsignedIntArrayFootprintSmallerThanIntegerArray() {
        // Given
        int n = 100;
        UnsignedInt[] sut = new UnsignedInt[n];
        for (int i = 0; i < n; i++) sut[i] = new UnsignedInt(i);
        Integer[] boxedArray = new Integer[n];
        for (int i = 256; i < 256 + n; i++) boxedArray[i - 256] = i; // avoid JVM Integer cache

        // When
        // UnsignedInt[] stores values inline — array shell includes element data.
        // Integer[] stores references; each Integer object is a separate heap allocation.
        long result = ClassLayout.parseInstance(sut).instanceSize();
        long boxedTotal = ClassLayout.parseInstance(boxedArray).instanceSize()
                + (long) n * ClassLayout.parseClass(Integer.class).instanceSize();

        System.out.printf("UnsignedInt[%d]: %d bytes (inline)%n", n, result);
        System.out.printf("   Integer[%d]: %d bytes (array shell + %d objects)%n", n, boxedTotal, n);

        // Then
        assertThat(result).isLessThan(boxedTotal);
    }
}
