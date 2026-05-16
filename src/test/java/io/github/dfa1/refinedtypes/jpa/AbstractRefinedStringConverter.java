package io.github.dfa1.refinedtypes.jpa;

import io.github.dfa1.refinedtypes.RefinedString;
import jakarta.persistence.AttributeConverter;

/// Base JPA converter for value classes that implement {@link RefinedString}.
///
/// Subclasses supply only the factory that reconstructs the value class from
/// its raw column `String`. The null contract follows JPA semantics:
/// `null` column ↔ `null` attribute — even though a live value-class instance
/// is never null at runtime.
abstract class AbstractRefinedStringConverter<T extends RefinedString<T>>
        implements AttributeConverter<T, String> {

    /// Reconstruct the value class from its stored column value.
    protected abstract T fromString(String value);

    @Override
    public String convertToDatabaseColumn(T attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        return dbData == null ? null : fromString(dbData);
    }
}
