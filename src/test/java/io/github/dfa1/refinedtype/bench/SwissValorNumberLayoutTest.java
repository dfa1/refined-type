package io.github.dfa1.refinedtype.bench;

import io.github.dfa1.refinedtype.examples.SwissValorNumber;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;

import static org.assertj.core.api.Assertions.assertThat;

class SwissValorNumberLayoutTest {

    @Test
    void printLayouts() {
        // NOTE: instanceSize() from JOL shows the *boxed/heap* form of a value class —
        // what the JVM allocates when the value escapes to Object. It includes the full
        // object header (12 bytes) plus alignment padding and is NOT what gets stored in
        // a typed SwissValorNumber[] array. In a flat typed array only the payload (4 bytes
        // for an int) is stored per element — see printArrayFootprintFor10K() for proof.
        System.out.println("=== value class SwissValorNumber (boxed/heap form) ===");
        System.out.println(ClassLayout.parseClass(SwissValorNumber.class).toPrintable());

        System.out.println("=== identity class SwissValorNumberIdentity ===");
        System.out.println(ClassLayout.parseClass(SwissValorNumberIdentity.class).toPrintable());

        System.out.printf("SwissValorNumber         boxed heap size : %d bytes (header + int + padding)%n",
                ClassLayout.parseClass(SwissValorNumber.class).instanceSize());
        System.out.printf("SwissValorNumberIdentity heap size       : %d bytes%n",
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
        // Actual bytes per element in the flat value array (subtract 16-byte array header)
        long bytesPerValueElement = (valueShell - 16) / n;

        System.out.printf("SwissValorNumber[%d]         total: %,d bytes  (%d bytes/element flat — payload only)%n",
                n, valueShell, bytesPerValueElement);
        System.out.printf("SwissValorNumberIdentity[%d] total: %,d bytes  (%d-byte refs + %,d objects × %d bytes)%n",
                n, identityTotal, 4, n, identityObj);
        System.out.printf("Memory ratio (identity/value): %.2fx%n",
                (double) identityTotal / valueShell);

        // Given
        // When (computed above)
        // Then
        assertThat(bytesPerValueElement)
                .as("value class array must store only the int payload (4 bytes/element)")
                .isEqualTo(4);
        assertThat(identityTotal)
                .as("identity class total footprint must be at least 4× the value class array")
                .isGreaterThanOrEqualTo(4 * valueShell);
    }
}
