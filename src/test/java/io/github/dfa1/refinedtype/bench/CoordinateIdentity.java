package io.github.dfa1.refinedtype.bench;

/// Identity-class mirror of {@link io.github.dfa1.refinedtype.examples.Coordinate}.
/// Used only in benchmarks to isolate the cost of heap allocation and pointer
/// indirection versus value-class flat inlining in arrays.
/// Haversine math is identical to the value-class implementation.
class CoordinateIdentity {

    static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private final double latitude;
    private final double longitude;

    CoordinateIdentity(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    double distanceTo(CoordinateIdentity other) {
        double phi1 = Math.toRadians(latitude);
        double phi2 = Math.toRadians(other.latitude);
        double dPhi = phi2 - phi1;
        double dLambda = Math.toRadians(other.longitude) - Math.toRadians(longitude);

        double sinDPhi2 = Math.sin(dPhi / 2.0);
        double sinDLambda2 = Math.sin(dLambda / 2.0);

        double a = sinDPhi2 * sinDPhi2
                 + Math.cos(phi1) * Math.cos(phi2) * sinDLambda2 * sinDLambda2;
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return EARTH_RADIUS_METERS * c;
    }
}
