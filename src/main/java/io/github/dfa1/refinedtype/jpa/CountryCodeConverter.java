package io.github.dfa1.refinedtype.jpa;

import io.github.dfa1.refinedtype.examples.CountryCode;
import jakarta.persistence.Converter;

/// JPA {@link jakarta.persistence.AttributeConverter} for {@link CountryCode}.
///
/// Stores the ISO 3166-1 alpha-2 code as a two-character `CHAR` column.
/// `CountryCode` normalizes to uppercase on construction, so lowercase input
/// from legacy rows is corrected on load.
@Converter
public class CountryCodeConverter extends AbstractRefinedStringConverter<CountryCode> {

    @Override
    protected CountryCode fromString(String value) {
        return new CountryCode(value);
    }
}
