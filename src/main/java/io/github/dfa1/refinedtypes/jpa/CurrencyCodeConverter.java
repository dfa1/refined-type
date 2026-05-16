package io.github.dfa1.refinedtypes.jpa;

import io.github.dfa1.refinedtypes.examples.CurrencyCode;
import jakarta.persistence.Converter;

/// JPA {@link jakarta.persistence.AttributeConverter} for {@link CurrencyCode}.
///
/// Stores the ISO 4217 code as a three-character `CHAR` column.
/// `CurrencyCode` normalizes to uppercase on construction, so lowercase
/// input from legacy rows is corrected on load.
@Converter
public class CurrencyCodeConverter extends AbstractRefinedStringConverter<CurrencyCode> {

    @Override
    protected CurrencyCode fromString(String value) {
        return new CurrencyCode(value);
    }
}
