/// Ready-to-use refined types — illustrative implementations of the
/// pattern defined in the parent package.
///
/// Each class encodes a domain constraint into the type system:
///
/// | Class                                                           | Constraint                                           |
/// |-----------------------------------------------------------------|------------------------------------------------------|
/// | {@link io.github.dfa1.refinedtype.examples.Age}                | integer in `[0, 150]`                                |
/// | {@link io.github.dfa1.refinedtype.examples.AudioSample}        | signed 16-bit PCM sample                             |
/// | {@link io.github.dfa1.refinedtype.examples.Bic}                | ISO 9362 bank identifier (8 or 11 chars, canonical 11-char) |
/// | {@link io.github.dfa1.refinedtype.examples.CountryCode}        | ISO 3166-1 alpha-2 code (two uppercase letters)      |
/// | {@link io.github.dfa1.refinedtype.examples.CurrencyCode}       | ISO 4217 currency code (three uppercase letters)     |
/// | {@link io.github.dfa1.refinedtype.examples.CusipNumber}        | 9-char CUSIP (US/Canadian securities), → ISIN        |
/// | {@link io.github.dfa1.refinedtype.examples.Distance}           | non-negative metres (with `Unit` enum: M/KM/MILE/NM) |
/// | {@link io.github.dfa1.refinedtype.examples.Email}              | coarse syntactic email check                         |
/// | {@link io.github.dfa1.refinedtype.examples.Coordinate}         | (Latitude, Longitude) pair with haversine `Distance` |
/// | {@link io.github.dfa1.refinedtype.examples.HostName}           | RFC 1123 hostname with SSRF guards                   |
/// | {@link io.github.dfa1.refinedtype.examples.Iban}               | ISO 13616 bank account number (MOD 97-10 checksum)   |
/// | {@link io.github.dfa1.refinedtype.examples.Isin}               | ISO 6166 securities identifier                       |
/// | {@link io.github.dfa1.refinedtype.examples.Latitude}           | decimal degrees in `[-90, 90]`                       |
/// | {@link io.github.dfa1.refinedtype.examples.Longitude}          | decimal degrees in `[-180, 180]`                     |
/// | {@link io.github.dfa1.refinedtype.examples.Percentage}         | finite float in `[0, 100]`                           |
/// | {@link io.github.dfa1.refinedtype.examples.Port}               | TCP/UDP port in `[0, 65535]`                         |
/// | {@link io.github.dfa1.refinedtype.examples.Price}              | strictly positive, finite float                      |
/// | {@link io.github.dfa1.refinedtype.examples.Probability}        | finite float in `[0, 1]`                             |
/// | {@link io.github.dfa1.refinedtype.examples.Size}               | non-negative byte count (with `Unit` enum)           |
/// | {@link io.github.dfa1.refinedtype.examples.Slug}               | URL-safe lowercase identifier                        |
/// | {@link io.github.dfa1.refinedtype.examples.SwissValorNumber}   | SIX Valoren-Nummer, `[1, 999_999_999]`, → ISIN       |
/// | {@link io.github.dfa1.refinedtype.examples.Velocity}           | non-negative float (m/s)                             |
/// | {@link io.github.dfa1.refinedtype.examples.Volume}             | non-negative, finite float                           |
///
/// These are *examples* — they are meant to be copied, adapted, or
/// replaced by domain types in a real project, not consumed as a stable
/// API. The point is the pattern, not the catalogue.
package io.github.dfa1.refinedtype.examples;
