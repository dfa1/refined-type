package io.github.dfa1.refinedtypes;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

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
}
