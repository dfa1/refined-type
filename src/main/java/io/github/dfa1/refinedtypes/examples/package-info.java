/// Ready-to-use refined types — illustrative implementations of the
/// pattern defined in the parent package.
///
/// Each class encodes a domain constraint into the type system:
///
/// | Class                                                           | Constraint                                           |
/// |-----------------------------------------------------------------|------------------------------------------------------|
/// | {@link io.github.dfa1.refinedtypes.examples.Age}                | integer in `[0, 150]`                                |
/// | {@link io.github.dfa1.refinedtypes.examples.AudioSample}        | signed 16-bit PCM sample                             |
/// | {@link io.github.dfa1.refinedtypes.examples.Country}            | ISO 3166-1 alpha-2 code (two uppercase letters)      |
/// | {@link io.github.dfa1.refinedtypes.examples.CusipNumber}        | 9-char CUSIP (US/Canadian securities), → ISIN        |
/// | {@link io.github.dfa1.refinedtypes.examples.Email}              | coarse syntactic email check                         |
/// | {@link io.github.dfa1.refinedtypes.examples.GeoPoint}           | (Latitude, Longitude) pair with haversine distance   |
/// | {@link io.github.dfa1.refinedtypes.examples.HostName}           | RFC 1123 hostname with SSRF guards                   |
/// | {@link io.github.dfa1.refinedtypes.examples.Isin}               | ISO 6166 securities identifier                       |
/// | {@link io.github.dfa1.refinedtypes.examples.Latitude}           | decimal degrees in `[-90, 90]`                       |
/// | {@link io.github.dfa1.refinedtypes.examples.Longitude}          | decimal degrees in `[-180, 180]`                     |
/// | {@link io.github.dfa1.refinedtypes.examples.Percentage}         | finite float in `[0, 100]`                           |
/// | {@link io.github.dfa1.refinedtypes.examples.Port}               | TCP/UDP port in `[0, 65535]`                         |
/// | {@link io.github.dfa1.refinedtypes.examples.Price}              | strictly positive, finite float                      |
/// | {@link io.github.dfa1.refinedtypes.examples.Probability}        | finite float in `[0, 1]`                             |
/// | {@link io.github.dfa1.refinedtypes.examples.Size}               | non-negative byte count (with `Unit` enum)           |
/// | {@link io.github.dfa1.refinedtypes.examples.Slug}               | URL-safe lowercase identifier                        |
/// | {@link io.github.dfa1.refinedtypes.examples.SwissValorNumber}   | SIX Valoren-Nummer, `[1, 999_999_999]`, → ISIN       |
/// | {@link io.github.dfa1.refinedtypes.examples.Velocity}           | non-negative float (m/s)                             |
/// | {@link io.github.dfa1.refinedtypes.examples.Volume}             | non-negative, finite float                           |
///
/// These are *examples* — they are meant to be copied, adapted, or
/// replaced by domain types in a real project, not consumed as a stable
/// API. The point is the pattern, not the catalogue.
package io.github.dfa1.refinedtypes.examples;
