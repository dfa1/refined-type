package io.github.dfa1.refinedtype.examples;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CoordinateTest {

    // Real city coordinates used as reference points
    private static final Coordinate LONDON = Coordinate.of(Latitude.of(51.5074),  Longitude.of(  -0.1278));
    private static final Coordinate PARIS  = Coordinate.of(Latitude.of(48.8566),  Longitude.of(   2.3522));
    private static final Coordinate NYC    = Coordinate.of(Latitude.of(40.7128),  Longitude.of( -74.0060));
    private static final Coordinate LA     = Coordinate.of(Latitude.of(34.0522),  Longitude.of(-118.2437));
    private static final Coordinate TOKYO  = Coordinate.of(Latitude.of(35.6762),  Longitude.of( 139.6503));
    private static final Coordinate SYDNEY = Coordinate.of(Latitude.of(-33.8688), Longitude.of( 151.2093));

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void exposesLatitudeAndLongitude() {
        // Given
        var sut = Coordinate.of(Latitude.of(51.5074), Longitude.of(-0.1278));

        // When / Then
        assertThat(sut.latitude().value()).isEqualTo(51.5074);
        assertThat(sut.longitude().value()).isEqualTo(-0.1278);
    }

    // ── distanceTo — basic ──────────────────────────────────────────────────

    @Test
    void distanceToSelfIsZero() {
        // Given
        var sut = LONDON;

        // When
        Distance result = sut.distanceTo(sut);

        // Then
        assertThat(result.to(Distance.Unit.M)).isZero();
    }

    @Test
    void distanceIsSymmetric() {
        // Given / When
        Distance a = LONDON.distanceTo(PARIS);
        Distance b = PARIS.distanceTo(LONDON);

        // Then
        assertThat(a.to(Distance.Unit.M)).isEqualTo(b.to(Distance.Unit.M));
    }

    // ── distanceTo — known city pairs ───────────────────────────────────────

    @Test
    void londonToParisAroundThreeForty() {
        // Reference (online haversine calculators): ~343 556 m
        // Given / When
        Distance result = LONDON.distanceTo(PARIS);

        // Then — 1 % tolerance covers haversine vs WGS-84 spread
        assertThat(result.to(Distance.Unit.M)).isCloseTo(343_556, within(3_500.0));
    }

    @Test
    void nycToLosAngelesAroundThreeNineFour() {
        // Reference: ~3 935 700 m
        // Given / When
        Distance result = NYC.distanceTo(LA);

        // Then
        assertThat(result.to(Distance.Unit.M)).isCloseTo(3_935_700, within(40_000.0));
    }

    @Test
    void londonToNycAroundFiveFiveSeven() {
        // Reference: ~5 570 000 m
        // Given / When
        Distance result = LONDON.distanceTo(NYC);

        // Then
        assertThat(result.to(Distance.Unit.M)).isCloseTo(5_570_000, within(56_000.0));
    }

    @Test
    void tokyoToSydneyAroundSevenEightTwoSix() {
        // Reference: ~7 826 000 m
        // Given / When
        Distance result = TOKYO.distanceTo(SYDNEY);

        // Then
        assertThat(result.to(Distance.Unit.M)).isCloseTo(7_826_000, within(80_000.0));
    }

    // ── distanceTo — antipodal sanity check ─────────────────────────────────

    @Test
    void antipodalDistanceIsHalfCircumference() {
        // Given — south-pole equivalent of (0, 0) is (0, 180)
        var origin = Coordinate.of(Latitude.ZERO, Longitude.ZERO);
        var antipode = Coordinate.of(Latitude.ZERO, Longitude.of(180.0));

        // When
        Distance result = origin.distanceTo(antipode);

        // Then — π · R
        assertThat(result.to(Distance.Unit.M)).isCloseTo(Math.PI * Coordinate.EARTH_RADIUS_METERS, within(1.0));
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesBothAxes() {
        // Given
        var sut = Coordinate.of(Latitude.of(40.7128), Longitude.of(-74.0060));

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("Coordinate(40.7128, -74.006)");
    }
}
