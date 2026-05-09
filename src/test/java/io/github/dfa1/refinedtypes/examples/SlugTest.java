package io.github.dfa1.refinedtypes.examples;

import io.github.dfa1.refinedtypes.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SlugTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void simpleAlphanumericAccepted() {
        // Given
        var sut = new Slug("hello");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("hello");
    }

    @Test
    void hyphenatedAccepted() {
        // Given
        var sut = new Slug("hello-world");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("hello-world");
    }

    @Test
    void digitsAccepted() {
        // Given
        var sut = new Slug("post-2025-04");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("post-2025-04");
    }

    @Test
    void singleCharacterAccepted() {
        // Given
        var sut = new Slug("a");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("a");
    }

    @Test
    void singleDigitAccepted() {
        // Given
        var sut = new Slug("7");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("7");
    }

    @Test
    void exactly64CharsAccepted() {
        // Given
        String input = "a".repeat(64);

        // When
        var sut = new Slug(input);

        // Then
        assertThat(sut.value()).isEqualTo(input);
    }

    // ── rejection: empty / size ─────────────────────────────────────────────

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> new Slug(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyRejected() {
        // When / Then
        assertThatThrownBy(() -> new Slug(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void over64CharsRejected() {
        // Given
        String input = "a".repeat(65);

        // When / Then
        assertThatThrownBy(() -> new Slug(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── rejection: format ───────────────────────────────────────────────────

    @Test
    void uppercaseRejected() {
        // Slug is by convention lowercase — caller must slugify upstream
        // When / Then
        assertThatThrownBy(() -> new Slug("Hello"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leadingHyphenRejected() {
        // When / Then
        assertThatThrownBy(() -> new Slug("-bad"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void trailingHyphenRejected() {
        // When / Then
        assertThatThrownBy(() -> new Slug("bad-"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void doubleHyphenRejected() {
        // When / Then
        assertThatThrownBy(() -> new Slug("a--b"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void spaceRejected() {
        // When / Then
        assertThatThrownBy(() -> new Slug("hello world"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void underscoreRejected() {
        // When / Then
        assertThatThrownBy(() -> new Slug("hello_world"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void dotRejected() {
        // When / Then
        assertThatThrownBy(() -> new Slug("v1.2"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── rejection: injection vectors ────────────────────────────────────────

    @Test
    void pathTraversalRejected() {
        // ".." cannot survive — slugs cannot escape a routing prefix
        // When / Then
        assertThatThrownBy(() -> new Slug(".."))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void slashRejected() {
        // When / Then
        assertThatThrownBy(() -> new Slug("etc/passwd"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void percentEncodedSlashRejected() {
        // %2F could otherwise smuggle a slash past naive validation
        // When / Then
        assertThatThrownBy(() -> new Slug("etc%2fpasswd"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void crlfRejected() {
        // CR/LF would split HTTP headers if echoed in Set-Cookie / Location
        // When / Then
        assertThatThrownBy(() -> new Slug("evil\r\nSet-Cookie: x=y"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nulByteRejected() {
        // When / Then
        assertThatThrownBy(() -> new Slug("evil\u0000"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = new Slug("alpha");
        var other = new Slug("beta");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqual() {
        // Given
        var sut = new Slug("same");
        var other = new Slug("same");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringFormat() {
        // Given
        var sut = new Slug("my-post");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Slug(my-post)");
    }

    // ── RefinedString ───────────────────────────────────────────────────────

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = new Slug("hello-world");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("hello-world");
    }
}
