package io.github.dfa1.refinedtypes.jpa;

import io.github.dfa1.refinedtypes.examples.Age;
import jakarta.persistence.Converter;

/// JPA {@link jakarta.persistence.AttributeConverter} for {@link Age}.
///
/// Maps the age in whole years to a JDBC `SMALLINT` column. A `NULL` column
/// maps to `null` in the entity field; the caller is responsible for deciding
/// whether a nullable age is valid in their domain model.
@Converter
public class AgeConverter extends AbstractRefinedShortConverter<Age> {

    @Override
    protected Age fromShort(short value) {
        return new Age(value);
    }
}
