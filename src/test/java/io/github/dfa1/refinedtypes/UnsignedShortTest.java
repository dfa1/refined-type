package io.github.dfa1.refinedtypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnsignedShortTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zero_is_valid() {
        var u = new UnsignedShort(0);
        assertEquals(0, u.value());
    }

    @Test
    void max_value_is_valid() {
        var u = new UnsignedShort(UnsignedShort.MAX_VALUE); // 65_535
        assertEquals(65_535, u.value());
    }

    @Test
    void negative_is_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedShort(-1));
    }

    @Test
    void above_max_is_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedShort(65_536));
    }

    // ── unsigned bit-pattern semantics ──────────────────────────────────────

    @Test
    void value_above_signed_max_stored_correctly() {
        // 2^15 = 32_768 — raw bits are Short.MIN_VALUE (negative signed),
        // but Short.toUnsignedInt must recover the correct unsigned value.
        int unsignedPow15 = 32_768;
        var u = new UnsignedShort(unsignedPow15);

        assertEquals(unsignedPow15, u.value());
        assertEquals(Short.MIN_VALUE, u.rawBits()); // confirm the bit-pattern trick
    }

    @Test
    void value_preserves_all_16_bits() {
        // 0xBEEF = 48_879
        int beef = 0xBEEF;
        var u = new UnsignedShort(beef);
        assertEquals(beef, u.value());
        assertEquals(48_879, Short.toUnsignedInt(u.rawBits()));
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void comparison_uses_unsigned_order() {
        // 32_768 > 1 in unsigned space,
        // but raw bits of 32_768 are negative — signed compare would flip the result.
        var big = new UnsignedShort(32_768);
        var small = new UnsignedShort(1);

        assertTrue(big.compareTo(small) > 0);
        assertTrue(small.compareTo(big) < 0);
        assertEquals(0, big.compareTo(new UnsignedShort(32_768)));
    }

    @Test
    void max_is_greater_than_signed_max() {
        var max = new UnsignedShort(UnsignedShort.MAX_VALUE);
        var signedMax = new UnsignedShort(Short.MAX_VALUE); // 32_767

        assertTrue(max.compareTo(signedMax) > 0);
    }

    // ── Short unsigned API ───────────────────────────────────────────────────

    @Test
    void to_unsigned_int_round_trips() {
        var u = new UnsignedShort(65_535);
        assertEquals(65_535, Short.toUnsignedInt(u.rawBits()));
    }

    @Test
    void to_unsigned_long_round_trips() {
        var u = new UnsignedShort(65_535);
        assertEquals(65_535L, Short.toUnsignedLong(u.rawBits()));
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toString_shows_unsigned_representation() {
        var u = new UnsignedShort(UnsignedShort.MAX_VALUE);
        assertEquals("UnsignedShort(65535)", u.toString());
    }

    @Test
    void toString_zero() {
        assertEquals("UnsignedShort(0)", new UnsignedShort(0).toString());
    }
}
