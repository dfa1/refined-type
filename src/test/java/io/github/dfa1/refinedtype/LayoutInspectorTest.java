package io.github.dfa1.refinedtype;

import io.github.dfa1.refinedtype.examples.*;
import io.github.dfa1.refinedtype.fp.Float16;
import io.github.dfa1.refinedtype.unsigned.UnsignedByte;
import io.github.dfa1.refinedtype.unsigned.UnsignedInt;
import io.github.dfa1.refinedtype.unsigned.UnsignedLong;
import io.github.dfa1.refinedtype.unsigned.UnsignedShort;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LayoutInspectorTest {

    @Test
    void printLayouts() {
        System.out.println(VM.current().details());

        System.out.println("=== object layout ===");
        System.out.println(ClassLayout.parseClass(UnsignedInt.class).toPrintable());
        System.out.println(ClassLayout.parseClass(Integer.class).toPrintable());

        System.out.println("=== array layout ===");
        System.out.println(ClassLayout.parseClass(UnsignedInt[].class).toPrintable());
        System.out.println(ClassLayout.parseClass(Integer[].class).toPrintable());
    }

    @Test
    void printAllRefinedTypeSizes() {
        Map<String, Class<?>[]> groups = new LinkedHashMap<>();
        groups.put("RefinedInt  (raw: 4 bytes)", new Class<?>[]{ Age.class, Port.class, SwissValorNumber.class, UnsignedInt.class });
        groups.put("RefinedShort (raw: 2 bytes)", new Class<?>[]{ AudioSample.class, UnsignedShort.class });
        groups.put("RefinedLong  (raw: 8 bytes)", new Class<?>[]{ Size.class, UnsignedLong.class });
        groups.put("RefinedDouble (raw: 8 bytes)", new Class<?>[]{ Distance.class, Latitude.class, Longitude.class, Price.class });
        groups.put("RefinedFloat (raw: 4 bytes)", new Class<?>[]{ Percentage.class, Probability.class, Speed.class, Volume.class });
        groups.put("RefinedString (raw: ref)", new Class<?>[]{ CountryCode.class, CurrencyCode.class, CusipNumber.class, Email.class, HostName.class, Isin.class, Slug.class });
        groups.put("Other", new Class<?>[]{ Coordinate.class, Float16.class, UnsignedByte.class });

        System.out.println();
        System.out.printf("%-14s  %-22s  %s%n", "Group", "Type", "bytes");
        System.out.println("-".repeat(60));
        for (var entry : groups.entrySet()) {
            for (Class<?> cls : entry.getValue()) {
                long size = ClassLayout.parseClass(cls).instanceSize();
                System.out.printf("%-14s  %-22s  %d%n", entry.getKey(), cls.getSimpleName(), size);
            }
            System.out.println();
        }
    }

    @Test
    void unsignedIntArrayFootprintSmallerThanIntegerArray() {
        // Given
        int n = 100;
        UnsignedInt[] sut = new UnsignedInt[n];
        for (int i = 0; i < n; i++) sut[i] = UnsignedInt.of(i);
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
