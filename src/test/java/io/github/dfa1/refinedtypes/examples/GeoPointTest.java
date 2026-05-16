package io.github.dfa1.refinedtypes.examples;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class GeoPointTest {

    // Real city coordinates used as reference points
    private static final GeoPoint LONDON = new GeoPoint(new Latitude(51.5074),  new Longitude(  -0.1278));
    private static final GeoPoint PARIS  = new GeoPoint(new Latitude(48.8566),  new Longitude(   2.3522));
    private static final GeoPoint NYC    = new GeoPoint(new Latitude(40.7128),  new Longitude( -74.0060));
    private static final GeoPoint LA     = new GeoPoint(new Latitude(34.0522),  new Longitude(-118.2437));
    private static final GeoPoint TOKYO  = new GeoPoint(new Latitude(35.6762),  new Longitude( 139.6503));
    private static final GeoPoint SYDNEY = new GeoPoint(new Latitude(-33.8688), new Longitude( 151.2093));

    // ── construction ────────────────────────────────────────────────────────

    @Test
    void exposesLatitudeAndLongitude() {
        // Given
        var sut = new GeoPoint(new Latitude(51.5074), new Longitude(-0.1278));

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
        double result = sut.distanceTo(sut);

        // Then
        assertThat(result).isZero();
    }

    @Test
    void distanceIsSymmetric() {
        // Given / When
        double a = LONDON.distanceTo(PARIS);
        double b = PARIS.distanceTo(LONDON);

        // Then
        assertThat(a).isEqualTo(b);
    }

    // ── distanceTo — known city pairs ───────────────────────────────────────

    @Test
    void londonToParisAroundThreeForty() {
        // Reference (online haversine calculators): ~343 556 m
        // Given / When
        double result = LONDON.distanceTo(PARIS);

        // Then — 1 % tolerance covers haversine vs WGS-84 spread
        assertThat(result).isCloseTo(343_556, within(3_500.0));
    }

    @Test
    void nycToLosAngelesAroundThreeNineFour() {
        // Reference: ~3 935 700 m
        // Given / When
        double result = NYC.distanceTo(LA);

        // Then
        assertThat(result).isCloseTo(3_935_700, within(40_000.0));
    }

    @Test
    void londonToNycAroundFiveFiveSeven() {
        // Reference: ~5 570 000 m
        // Given / When
        double result = LONDON.distanceTo(NYC);

        // Then
        assertThat(result).isCloseTo(5_570_000, within(56_000.0));
    }

    @Test
    void tokyoToSydneyAroundSevenEightTwoSix() {
        // Reference: ~7 826 000 m
        // Given / When
        double result = TOKYO.distanceTo(SYDNEY);

        // Then
        assertThat(result).isCloseTo(7_826_000, within(80_000.0));
    }

    // ── distanceTo — antipodal sanity check ─────────────────────────────────

    @Test
    void antipodalDistanceIsHalfCircumference() {
        // Given — south-pole equivalent of (0, 0) is (0, 180)
        var origin = new GeoPoint(Latitude.ZERO, Longitude.ZERO);
        var antipode = new GeoPoint(Latitude.ZERO, new Longitude(180.0));

        // When
        double result = origin.distanceTo(antipode);

        // Then — π · R
        assertThat(result).isCloseTo(Math.PI * GeoPoint.EARTH_RADIUS_METERS, within(1.0));
    }

    // ── toString ────────────────────────────────────────────────────────────

    @Test
    void toStringIncludesBothAxes() {
        // Given
        var sut = new GeoPoint(new Latitude(40.7128), new Longitude(-74.0060));

        // When
        String result = sut.toString();

        // Then
        assertThat(result).isEqualTo("GeoPoint(40.7128, -74.006)");
    }
}
