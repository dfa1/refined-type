package io.github.dfa1.refinedtypes;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentMapCacheTest {

    @Test
    void factoryCalledOnFirstAccess() {
        // Given
        var sut = new ConcurrentMapCache();
        var calls = new AtomicInteger(0);

        // When
        sut.get(Country.class, "DE", () -> { calls.incrementAndGet(); return new Country("DE"); });

        // Then
        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    void factoryNotCalledOnCacheHit() {
        // Given
        var sut = new ConcurrentMapCache();
        var calls = new AtomicInteger(0);
        sut.get(Country.class, "DE", () -> { calls.incrementAndGet(); return new Country("DE"); });

        // When
        sut.get(Country.class, "DE", () -> { calls.incrementAndGet(); return new Country("DE"); });

        // Then
        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    void differentKeysStoredSeparately() {
        // Given
        var sut = new ConcurrentMapCache();

        // When
        sut.get(Country.class, "DE", () -> new Country("DE"));
        sut.get(Country.class, "US", () -> new Country("US"));

        // Then
        assertThat(sut.size(Country.class)).isEqualTo(2);
    }

    @Test
    void differentTypesStoredSeparately() {
        // Given
        var sut = new ConcurrentMapCache();

        // When
        sut.get(Country.class, "DE", () -> new Country("DE"));
        sut.get(Email.class, "a@b.com", () -> new Email("a@b.com"));

        // Then
        assertThat(sut.size(Country.class)).isEqualTo(1);
        assertThat(sut.size(Email.class)).isEqualTo(1);
    }

    @Test
    void sizeZeroForUnseenType() {
        // Given
        var sut = new ConcurrentMapCache();

        // When
        int result = sut.size(Country.class);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void usagePatternStaticFactory() {
        // Intended use: caller wraps construction with the cache,
        // e.g. in a static factory method on the LowCardinality type.
        // Given
        var sut = new ConcurrentMapCache();
        var calls = new AtomicInteger(0);

        // When — simulate two calls to Country.of("DE") backed by cache
        Country first  = sut.get(Country.class, "DE", () -> { calls.incrementAndGet(); return new Country("DE"); });
        Country second = sut.get(Country.class, "DE", () -> { calls.incrementAndGet(); return new Country("DE"); });

        // Then — constructor (and validation) runs once
        assertThat(calls.get()).isEqualTo(1);
        assertThat(first.value()).isEqualTo(second.value());
    }
}
