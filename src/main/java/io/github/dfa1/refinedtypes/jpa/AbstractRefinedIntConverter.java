package io.github.dfa1.refinedtypes.jpa;

import io.github.dfa1.refinedtypes.RefinedInt;
import jakarta.persistence.AttributeConverter;

/// Base JPA converter for value classes that implement {@link RefinedInt}.
///
/// Stores the wrapped `int` as an `Integer` column. The null contract mirrors
/// {@link AbstractRefinedStringConverter}: `null` column ↔ `null` attribute.
abstract class AbstractRefinedIntConverter<T extends RefinedInt<T>>
        implements AttributeConverter<T, Integer> {

    /// Reconstruct the value class from its stored column value.
    protected abstract T fromInt(int value);

    @Override
    public Integer convertToDatabaseColumn(T attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public T convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : fromInt(dbData);
    }
}
