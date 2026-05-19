package io.github.dfa1.refinedtype.examples;

import io.github.dfa1.refinedtype.RefinedString;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SlugTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void simpleAlphanumericAccepted() {
        // Given
        var sut = Slug.of("hello");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("hello");
    }

    @Test
    void hyphenatedAccepted() {
        // Given
        var sut = Slug.of("hello-world");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("hello-world");
    }

    @Test
    void digitsAccepted() {
        // Given
        var sut = Slug.of("post-2025-04");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("post-2025-04");
    }

    @Test
    void singleCharacterAccepted() {
        // Given
        var sut = Slug.of("a");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("a");
    }

    @Test
    void singleDigitAccepted() {
        // Given
        var sut = Slug.of("7");

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
        var sut = Slug.of(input);

        // Then
        assertThat(sut.value()).isEqualTo(input);
    }

    // ── rejection: empty / size ─────────────────────────────────────────────

    @Test
    void nullRejected() {
        // When / Then
        assertThatThrownBy(() -> Slug.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyRejected() {
        // When / Then
        assertThatThrownBy(() -> Slug.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void over64CharsRejected() {
        // Given
        String input = "a".repeat(65);

        // When / Then
        assertThatThrownBy(() -> Slug.of(input))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── rejection: format ───────────────────────────────────────────────────

    @Test
    void uppercaseRejected() {
        // Slug is by convention lowercase — caller must slugify upstream
        // When / Then
        assertThatThrownBy(() -> Slug.of("Hello"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leadingHyphenRejected() {
        // When / Then
        assertThatThrownBy(() -> Slug.of("-bad"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void trailingHyphenRejected() {
        // When / Then
        assertThatThrownBy(() -> Slug.of("bad-"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void doubleHyphenRejected() {
        // When / Then
        assertThatThrownBy(() -> Slug.of("a--b"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void spaceRejected() {
        // When / Then
        assertThatThrownBy(() -> Slug.of("hello world"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void underscoreRejected() {
        // When / Then
        assertThatThrownBy(() -> Slug.of("hello_world"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void dotRejected() {
        // When / Then
        assertThatThrownBy(() -> Slug.of("v1.2"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── rejection: injection vectors ────────────────────────────────────────

    @Test
    void pathTraversalRejected() {
        // ".." cannot survive — slugs cannot escape a routing prefix
        // When / Then
        assertThatThrownBy(() -> Slug.of(".."))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void slashRejected() {
        // When / Then
        assertThatThrownBy(() -> Slug.of("etc/passwd"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void percentEncodedSlashRejected() {
        // %2F could otherwise smuggle a slash past naive validation
        // When / Then
        assertThatThrownBy(() -> Slug.of("etc%2fpasswd"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void crlfRejected() {
        // CR/LF would split HTTP headers if echoed in Set-Cookie / Location
        // When / Then
        assertThatThrownBy(() -> Slug.of("evil\r\nSet-Cookie: x=y"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nulByteRejected() {
        // When / Then
        assertThatThrownBy(() -> Slug.of("evil\u0000"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void compareToReturnsNegativeWhenLexicographicallySmaller() {
        // Given
        var sut = Slug.of("alpha");
        var other = Slug.of("beta");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isNegative();
    }

    @Test
    void compareToReturnsZeroForEqual() {
        // Given
        var sut = Slug.of("same");
        var other = Slug.of("same");

        // When
        int result = sut.compareTo(other);

        // Then
        assertThat(result).isZero();
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringFormat() {
        // Given
        var sut = Slug.of("my-post");

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Slug(my-post)");
    }

    // ── RefinedString ───────────────────────────────────────────────────────

    @Test
    void implementsRefinedString() {
        // Given
        RefinedString sut = Slug.of("hello-world");

        // When
        String result = sut.value();

        // Then
        assertThat(result).isEqualTo("hello-world");
    }
}
