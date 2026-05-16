package io.github.dfa1.refinedtypes.jpa;

import io.github.dfa1.refinedtypes.examples.Port;
import jakarta.persistence.Converter;

/// JPA {@link jakarta.persistence.AttributeConverter} for {@link Port}.
///
/// Maps the TCP/UDP port number to a JDBC `INTEGER` column. Uses the
/// `Port(int)` constructor directly — the column type is numeric, not text.
@Converter
public class PortConverter extends AbstractRefinedIntConverter<Port> {

    @Override
    protected Port fromInt(int value) {
        return new Port(value);
    }
}
