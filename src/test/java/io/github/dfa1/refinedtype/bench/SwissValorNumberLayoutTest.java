package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.SwissValorNumber;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;

class SwissValorNumberLayoutTest {

    @Test
    void printLayouts() {
        System.out.println("=== value class SwissValorNumber ===");
        System.out.println(ClassLayout.parseClass(SwissValorNumber.class).toPrintable());

        System.out.println("=== identity class SwissValorNumberIdentity ===");
        System.out.println(ClassLayout.parseClass(SwissValorNumberIdentity.class).toPrintable());

        System.out.printf("SwissValorNumber         instance size: %d bytes%n",
                ClassLayout.parseClass(SwissValorNumber.class).instanceSize());
        System.out.printf("SwissValorNumberIdentity instance size: %d bytes%n",
                ClassLayout.parseClass(SwissValorNumberIdentity.class).instanceSize());
    }

    @Test
    void printArrayFootprintFor10K() {
        int n = 10_000;
        SwissValorNumber[] valueArr = new SwissValorNumber[n];
        SwissValorNumberIdentity[] identityArr = new SwissValorNumberIdentity[n];
        for (int i = 0; i < n; i++) {
            valueArr[i]    = new SwissValorNumber(i + 1);
            identityArr[i] = new SwissValorNumberIdentity(i + 1);
        }

        long valueShell    = ClassLayout.parseInstance(valueArr).instanceSize();
        long identityShell = ClassLayout.parseInstance(identityArr).instanceSize();
        long identityObj   = ClassLayout.parseClass(SwissValorNumberIdentity.class).instanceSize();
        long identityTotal = identityShell + (long) n * identityObj;

        System.out.printf("SwissValorNumber[%d]         array shell: %,d bytes (elements stored inline)%n",
                n, valueShell);
        System.out.printf("SwissValorNumberIdentity[%d] array shell: %,d bytes + %,d objects × %d bytes = %,d bytes total%n",
                n, identityShell, n, identityObj, identityTotal);
        System.out.printf("Memory ratio (identity/value): %.2fx%n",
                (double) identityTotal / valueShell);
    }
}
