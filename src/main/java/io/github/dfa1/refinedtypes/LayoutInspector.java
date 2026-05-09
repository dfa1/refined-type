package io.github.dfa1.refinedtypes;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

public class LayoutInspector {

    static final int ARRAY_SIZE = 5;

    public static void main(String[] args) {
        System.out.println(VM.current().details());

        System.out.println("=== single object layout ===");
        System.out.println(ClassLayout.parseClass(PositiveInt.class).toPrintable());
        System.out.println(ClassLayout.parseClass(Integer.class).toPrintable());

        System.out.println("=== array graph (size=" + ARRAY_SIZE + ") ===");

        PositiveInt[] valueArray = new PositiveInt[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            valueArray[i] = new PositiveInt(i + 1);
        }

        Integer[] boxedArray = new Integer[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            boxedArray[i] = i + 1;
        }

        System.out.println("PositiveInt[" + ARRAY_SIZE + "]:");
        System.out.println(GraphLayout.parseInstance((Object) valueArray).toPrintable());
        System.out.println("total: " + GraphLayout.parseInstance((Object) valueArray).totalSize() + " bytes");

        System.out.println();
        System.out.println("Integer[" + ARRAY_SIZE + "]:");
        System.out.println(GraphLayout.parseInstance((Object) boxedArray).toPrintable());
        System.out.println("total: " + GraphLayout.parseInstance((Object) boxedArray).totalSize() + " bytes");
    }
}
