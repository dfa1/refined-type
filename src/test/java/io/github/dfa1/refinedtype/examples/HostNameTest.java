package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HostNameTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void simpleHostAccepted() {
        // Given
        var sut = HostName.of("example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("example.com");
    }

    @Test
    void singleLabelNonLocalhostAccepted() {
        // Single-label hostnames are valid RFC-wise (e.g. internal search domains)
        // Given
        var sut = HostName.of("intranet");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("intranet");
    }

    @Test
    void multipleLabelsAccepted() {
        // Given
        var sut = HostName.of("api.eu-west.example.co.uk");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("api.eu-west.example.co.uk");
    }

    @Test
    void uppercaseNormalizedToLowercase() {
        // Given
        var sut = HostName.of("Example.COM");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("example.com");
    }

    @Test
    void hyphenInsideLabelAccepted() {
        // Given
        var sut = HostName.of("eu-west-1.example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("eu-west-1.example.com");
    }

    @Test
    void digitsAccepted() {
        // Given
        var sut = HostName.of("host42.example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("host42.example.com");
    }

    // ── SSRF guards ──────────────────────────────────────────────────────────

    @Test
    void localhostRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of("localhost"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void localhostCaseInsensitiveRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of("LOCALHOST"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loopbackIpv4Rejected() {
        // 127.0.0.1 — loopback
        // When / Then
        assertThatThrownBy(() -> HostName.of("127.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void privateClassAIpv4Rejected() {
        // 10.x.x.x — RFC 1918 class A
        // When / Then
        assertThatThrownBy(() -> HostName.of("10.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void privateClassBIpv4Rejected() {
        // 172.16–31.x.x — RFC 1918 class B
        // When / Then
        assertThatThrownBy(() -> HostName.of("172.16.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void privateClassCIpv4Rejected() {
        // 192.168.x.x — RFC 1918 class C
        // When / Then
        assertThatThrownBy(() -> HostName.of("192.168.1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void awsMetadataEndpointRejected() {
        // 169.254.169.254 — AWS instance metadata (link-local)
        // When / Then
        assertThatThrownBy(() -> HostName.of("169.254.169.254"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void publicIpv4Accepted() {
        // 8.8.8.8 — Google public DNS, not in any blocked range
        // Given
        var sut = HostName.of("8.8.8.8");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("8.8.8.8");
    }

    @Test
    void hostWithPrivateSubstringNotBlocked() {
        // "10.example.com" has "10" as a label but is not an IPv4 literal
        // Given
        var sut = HostName.of("10.example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("10.example.com");
    }

    // ── rejection ───────────────────────────────────────────────────────────

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leadingHyphenRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of("-bad.example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void trailingHyphenRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of("bad-.example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leadingDotRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of(".example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void trailingDotRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of("example.com."))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void doubleDotRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of("a..b"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void underscoreRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of("bad_host.example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void spaceRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of("bad host"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void slashRejected() {
        // When / Then
        assertThatThrownBy(() -> HostName.of("evil.com/path"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void labelOver63CharsRejected() {
        // 64 'a' chars in one label
        // Given
        String input = "a".repeat(64) + ".example.com";

        // When / Then
        assertThatThrownBy(() -> HostName.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void exactly63CharLabelAccepted() {
        // Given
        String input = "a".repeat(63) + ".example.com";

        // When
        var sut = HostName.of(input);

        // Then
        assertThat(sut.value()).isEqualTo(input);
    }

    @Test
    void totalOver253CharsRejected() {
        // 4 labels of 63 + 3 dots = 255 > 253
        // Given
        String label = "a".repeat(63);
        String input = label + "." + label + "." + label + "." + label;

        // When / Then
        assertThatThrownBy(() -> HostName.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsZeroRegardlessOfInputCase() {
        // Given
        var sut = HostName.of("Example.Com");
        var other = HostName.of("example.com");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = HostName.of("a.example.com");
        var other = HostName.of("b.example.com");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringFormat() {
        // Given
        var sut = HostName.of("example.com");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("HostName(example.com)");
    }

    // ── RefinedString ───────────────────────────────────────────────────────

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = HostName.of("example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("example.com");
    }
}
