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
        var sut = new HostName("example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("example.com");
    }

    @Test
    void singleLabelNonLocalhostAccepted() {
        // Single-label hostnames are valid RFC-wise (e.g. internal search domains)
        // Given
        var sut = new HostName("intranet");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("intranet");
    }

    @Test
    void multipleLabelsAccepted() {
        // Given
        var sut = new HostName("api.eu-west.example.co.uk");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("api.eu-west.example.co.uk");
    }

    @Test
    void uppercaseNormalizedToLowercase() {
        // Given
        var sut = new HostName("Example.COM");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("example.com");
    }

    @Test
    void hyphenInsideLabelAccepted() {
        // Given
        var sut = new HostName("eu-west-1.example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("eu-west-1.example.com");
    }

    @Test
    void digitsAccepted() {
        // Given
        var sut = new HostName("host42.example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("host42.example.com");
    }

    // ── SSRF guards ──────────────────────────────────────────────────────────

    @Test
    void localhostRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName("localhost"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void localhostCaseInsensitiveRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName("LOCALHOST"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loopbackIpv4Rejected() {
        // 127.0.0.1 — loopback
        // When / Then
        assertThatThrownBy(() -> new HostName("127.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void privateClassAIpv4Rejected() {
        // 10.x.x.x — RFC 1918 class A
        // When / Then
        assertThatThrownBy(() -> new HostName("10.0.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void privateClassBIpv4Rejected() {
        // 172.16–31.x.x — RFC 1918 class B
        // When / Then
        assertThatThrownBy(() -> new HostName("172.16.0.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void privateClassCIpv4Rejected() {
        // 192.168.x.x — RFC 1918 class C
        // When / Then
        assertThatThrownBy(() -> new HostName("192.168.1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void awsMetadataEndpointRejected() {
        // 169.254.169.254 — AWS instance metadata (link-local)
        // When / Then
        assertThatThrownBy(() -> new HostName("169.254.169.254"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void publicIpv4Accepted() {
        // 8.8.8.8 — Google public DNS, not in any blocked range
        // Given
        var sut = new HostName("8.8.8.8");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("8.8.8.8");
    }

    @Test
    void hostWithPrivateSubstringNotBlocked() {
        // "10.example.com" has "10" as a label but is not an IPv4 literal
        // Given
        var sut = new HostName("10.example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("10.example.com");
    }

    // ── rejection ───────────────────────────────────────────────────────────

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leadingHyphenRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName("-bad.example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void trailingHyphenRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName("bad-.example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leadingDotRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName(".example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void trailingDotRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName("example.com."))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void doubleDotRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName("a..b"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void underscoreRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName("bad_host.example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void spaceRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName("bad host"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void slashRejected() {
        // When / Then
        assertThatThrownBy(() -> new HostName("evil.com/path"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void labelOver63CharsRejected() {
        // 64 'a' chars in one label
        // Given
        String input = "a".repeat(64) + ".example.com";

        // When / Then
        assertThatThrownBy(() -> new HostName(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void exactly63CharLabelAccepted() {
        // Given
        String input = "a".repeat(63) + ".example.com";

        // When
        var sut = new HostName(input);

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
        assertThatThrownBy(() -> new HostName(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsZeroRegardlessOfInputCase() {
        // Given
        var sut = new HostName("Example.Com");
        var other = new HostName("example.com");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = new HostName("a.example.com");
        var other = new HostName("b.example.com");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringFormat() {
        // Given
        var sut = new HostName("example.com");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("HostName(example.com)");
    }

    // ── RefinedString ───────────────────────────────────────────────────────

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = new HostName("example.com");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("example.com");
    }
}
