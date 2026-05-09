package io.github.dfa1.refinedtypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnsignedIntTest {

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void zero_is_valid() {
        var u = new UnsignedInt(0L);
        assertEquals(0L, u.value());
    }

    @Test
    void max_value_is_valid() {
        var u = new UnsignedInt(UnsignedInt.MAX_VALUE); // 4_294_967_295
        assertEquals(4_294_967_295L, u.value());
    }

    @Test
    void negative_long_is_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedInt(-1L));
    }

    @Test
    void above_max_is_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new UnsignedInt(4_294_967_296L));
    }

    // ── unsigned bit-pattern semantics ──────────────────────────────────────

    @Test
    void value_above_signed_max_stored_correctly() {
        // 2^31 = 2_147_483_648 — raw bits are Integer.MIN_VALUE (negative signed),
        // but Integer.toUnsignedLong must recover the correct unsigned value.
        long unsignedPow31 = 2_147_483_648L;
        var u = new UnsignedInt(unsignedPow31);

        assertEquals(unsignedPow31, u.value());
        assertEquals(Integer.MIN_VALUE, u.rawBits()); // confirm the bit-pattern trick
    }

    @Test
    void value_preserves_all_32_bits() {
        // 0xDEADBEEF = 3_735_928_559 — classic unsigned sentinel
        long deadbeef = 0xDEAD_BEEFL;
        var u = new UnsignedInt(deadbeef);
        assertEquals(deadbeef, u.value());
        assertEquals(Integer.toUnsignedString(u.rawBits()), "3735928559");
    }

    // ── comparison ──────────────────────────────────────────────────────────

    @Test
    void comparison_uses_unsigned_order() {
        // 2_147_483_648 > 1 in unsigned space,
        // but raw bits of 2_147_483_648 are negative — signed compare would flip the result.
        var big = new UnsignedInt(2_147_483_648L);
        var small = new UnsignedInt(1L);

        assertTrue(big.compareTo(small) > 0);
        assertTrue(small.compareTo(big) < 0);
        assertEquals(0, big.compareTo(new UnsignedInt(2_147_483_648L)));
    }

    @Test
    void max_is_greater_than_signed_max() {
        var max = new UnsignedInt(UnsignedInt.MAX_VALUE);
        var signedMax = new UnsignedInt(Integer.MAX_VALUE); // 2_147_483_647

        assertTrue(max.compareTo(signedMax) > 0);
    }

    // ── Integer unsigned API ─────────────────────────────────────────────────

    @Test
    void unsigned_divide_and_remainder() {
        // 4_000_000_000 / 3 = 1_333_333_333 remainder 1
        var dividend = new UnsignedInt(4_000_000_000L);
        var divisor = new UnsignedInt(3L);

        int q = Integer.divideUnsigned(dividend.rawBits(), divisor.rawBits());
        int r = Integer.remainderUnsigned(dividend.rawBits(), divisor.rawBits());

        assertEquals(1_333_333_333L, Integer.toUnsignedLong(q));
        assertEquals(1L, Integer.toUnsignedLong(r));
    }

    @Test
    void parse_unsigned_string_round_trips() {
        var u = new UnsignedInt(4_294_967_295L);
        String s = Integer.toUnsignedString(u.rawBits());
        assertEquals("4294967295", s);

        int parsed = Integer.parseUnsignedInt(s);
        assertEquals(u.rawBits(), parsed);
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toString_shows_unsigned_representation() {
        var u = new UnsignedInt(UnsignedInt.MAX_VALUE);
        assertEquals("UnsignedInt(4294967295)", u.toString());
    }
}
