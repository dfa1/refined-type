package io.github.dfa1.refinedtypes.examples;

/// Geographic coordinate — a {@link Latitude}/{@link Longitude} pair.
///
/// The pair is the natural carrier for geo operations that need both
/// axes (great-circle distance, bearing, midpoint). Operations that
/// depend on only one axis stay on the axis types themselves.
///
/// {@link #distanceTo(Coordinate)} returns the great-circle {@link Distance}
/// using the haversine formula on a spherical Earth (IUGG mean radius
/// `6_371_000` m). Accuracy versus the WGS-84 ellipsoid is within
/// roughly 0.5% for inter-continental distances; for sub-metre geodesy
/// use Vincenty or Karney instead.
public value class Coordinate {

    /// IUGG mean Earth radius in metres.
    public static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private final Latitude latitude;
    private final Longitude longitude;

    public Coordinate(Latitude latitude, Longitude longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Latitude latitude() {
        return latitude;
    }

    public Longitude longitude() {
        return longitude;
    }

    /// Great-circle distance (haversine, spherical Earth).
    public Distance distanceTo(Coordinate other) {
        double phi1 = latitude.toRadians();
        double phi2 = other.latitude.toRadians();
        double dPhi = phi2 - phi1;
        double dLambda = other.longitude.toRadians() - longitude.toRadians();

        double sinDPhi2 = Math.sin(dPhi / 2.0);
        double sinDLambda2 = Math.sin(dLambda / 2.0);

        double a = sinDPhi2 * sinDPhi2
                 + Math.cos(phi1) * Math.cos(phi2) * sinDLambda2 * sinDLambda2;
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return new Distance(EARTH_RADIUS_METERS * c);
    }

    @Override
    public String toString() {
        return "Coordinate(" + latitude.value() + ", " + longitude.value() + ")";
    }
}
