package io.github.dfa1.refinedtype.jpa;

import io.github.dfa1.refinedtype.examples.Email;
import jakarta.persistence.Converter;

/// JPA {@link jakarta.persistence.AttributeConverter} for {@link Email}.
///
/// Stores the validated email address as a `VARCHAR` column. Construction
/// of `Email` re-runs validation on read, so a row written by another
/// process is re-checked on load.
@Converter
public class EmailConverter extends AbstractRefinedStringConverter<Email> {

    @Override
    protected Email fromString(String value) {
        return new Email(value);
    }
}
