package io.github.dfa1.refinedtype.jpa;

import io.github.dfa1.refinedtype.RefinedShort;
import jakarta.persistence.AttributeConverter;

/// Base JPA converter for value classes that implement {@link RefinedShort}.
///
/// Stores the wrapped `short` as a `Short` (`SMALLINT`) column. The null
/// contract mirrors {@link AbstractRefinedIntConverter}: `null` column ↔
/// `null` attribute.
abstract class AbstractRefinedShortConverter<T extends RefinedShort<T>>
        implements AttributeConverter<T, Short> {

    /// Reconstruct the value class from its stored column value.
    protected abstract T fromShort(short value);

    @Override
    public Short convertToDatabaseColumn(T attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public T convertToEntityAttribute(Short dbData) {
        return dbData == null ? null : fromShort(dbData);
    }
}
