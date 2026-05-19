package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MacAddressTest {

    private static final String COLON_LOWER  = "00:1a:2b:3c:4d:5e";
    private static final String COLON_UPPER  = "00:1A:2B:3C:4D:5E";
    private static final String HYPHEN_LOWER = "00-1a-2b-3c-4d-5e";
    private static final String PLAIN_LOWER  = "001a2b3c4d5e";
    private static final String CANONICAL    = "00:1a:2b:3c:4d:5e";

    // ── construction / normalization ─────────────────────────────────────────

    @Test
    void colonLowercaseAccepted() {
        // Given / When
        var sut = MacAddress.of(COLON_LOWER);

        // Then
        assertThat(sut.value()).isEqualTo(CANONICAL);
    }

    @Test
    void colonUppercaseNormalizedToLower() {
        // Given / When
        var sut = MacAddress.of(COLON_UPPER);

        // Then
        assertThat(sut.value()).isEqualTo(CANONICAL);
    }

    @Test
    void hyphenSeparatedNormalizedToColons() {
        // Given / When
        var sut = MacAddress.of(HYPHEN_LOWER);

        // Then
        assertThat(sut.value()).isEqualTo(CANONICAL);
    }

    @Test
    void noSeparatorNormalized() {
        // Given / When
        var sut = MacAddress.of(PLAIN_LOWER);

        // Then
        assertThat(sut.value()).isEqualTo(CANONICAL);
    }

    @Test
    void allZerosAccepted() {
        // Given / When
        var sut = MacAddress.of("00:00:00:00:00:00");

        // Then
        assertThat(sut.value()).isEqualTo("00:00:00:00:00:00");
    }

    @Test
    void broadcastConstantCanonical() {
        assertThat(MacAddress.BROADCAST.value()).isEqualTo("ff:ff:ff:ff:ff:ff");
    }

    // ── rejection ────────────────────────────────────────────────────────────

    @Test
    void nullRejected() {
        assertThatThrownBy(() -> MacAddress.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyRejected() {
        assertThatThrownBy(() -> MacAddress.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooShortRejected() {
        assertThatThrownBy(() -> MacAddress.of("00:1a:2b:3c:4d"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void tooLongRejected() {
        assertThatThrownBy(() -> MacAddress.of("00:1a:2b:3c:4d:5e:6f"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void mixedSeparatorsRejected() {
        // colon and hyphen mixed in the same address
        assertThatThrownBy(() -> MacAddress.of("00:1a-2b:3c:4d:5e"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nonHexCharRejected() {
        assertThatThrownBy(() -> MacAddress.of("00:1g:2b:3c:4d:5e"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void singleOctetRejected() {
        assertThatThrownBy(() -> MacAddress.of("ab"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── octets ───────────────────────────────────────────────────────────────

    @Test
    void octetsReturnsSixValues() {
        // Given
        var sut = MacAddress.of("01:23:45:67:89:ab");

        // When
        int[] result = sut.octets();

        // Then
        assertThat(result).containsExactly(0x01, 0x23, 0x45, 0x67, 0x89, 0xab);
    }

    @Test
    void octetsAllZero() {
        // Given
        var sut = MacAddress.of("00:00:00:00:00:00");

        // When
        int[] result = sut.octets();

        // Then
        assertThat(result).containsExactly(0, 0, 0, 0, 0, 0);
    }

    @Test
    void octetsAllFF() {
        // Given
        var sut = MacAddress.BROADCAST;

        // When
        int[] result = sut.octets();

        // Then
        assertThat(result).containsExactly(0xff, 0xff, 0xff, 0xff, 0xff, 0xff);
    }

    // ── multicast ────────────────────────────────────────────────────────────

    @Test
    void unicastAddressIsNotMulticast() {
        // Given — first octet 0x00: LSB = 0
        var sut = MacAddress.of("00:1a:2b:3c:4d:5e");

        // When
        boolean result = sut.isMulticast();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void multicastBitSetDetected() {
        // Given — first octet 0x01: LSB = 1
        var sut = MacAddress.of("01:00:5e:00:00:01");

        // When
        boolean result = sut.isMulticast();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void broadcastIsMulticast() {
        // ff = 11111111b — LSB is 1
        assertThat(MacAddress.BROADCAST.isMulticast()).isTrue();
    }

    // ── locally administered ─────────────────────────────────────────────────

    @Test
    void globallyUniqueAddressNotLocallyAdministered() {
        // Given — first octet 0x00: bit 1 = 0
        var sut = MacAddress.of("00:1a:2b:3c:4d:5e");

        // When
        boolean result = sut.isLocallyAdministered();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void locallyAdministeredBitSetDetected() {
        // Given — first octet 0x02: bit 1 = 1
        var sut = MacAddress.of("02:00:00:00:00:01");

        // When
        boolean result = sut.isLocallyAdministered();

        // Then
        assertThat(result).isTrue();
    }

    // ── broadcast ────────────────────────────────────────────────────────────

    @Test
    void broadcastDetected() {
        assertThat(MacAddress.BROADCAST.isBroadcast()).isTrue();
    }

    @Test
    void nonBroadcastNotDetected() {
        assertThat(MacAddress.of("ff:ff:ff:ff:ff:fe").isBroadcast()).isFalse();
    }

    // ── comparison ───────────────────────────────────────────────────────────

    @Test
    void compareToOrdersLexicographically() {
        // Given
        var sut = MacAddress.of("00:00:00:00:00:01");
        var other = MacAddress.of("00:00:00:00:00:02");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToZeroForEqual() {
        // Given
        var sut = MacAddress.of(COLON_LOWER);
        var other = MacAddress.of(HYPHEN_LOWER); // same value, different input format

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Test
    void toStringFormat() {
        // Given
        var sut = MacAddress.of(COLON_LOWER);

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("MacAddress(00:1a:2b:3c:4d:5e)");
    }

    // ── RefinedString ────────────────────────────────────────────────────────

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString<?> sut = MacAddress.of(COLON_LOWER);

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo(CANONICAL);
    }
}
