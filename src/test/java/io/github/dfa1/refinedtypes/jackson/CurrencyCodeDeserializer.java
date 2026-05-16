package io.github.dfa1.refinedtypes.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.dfa1.refinedtypes.examples.CurrencyCode;

import java.io.IOException;

/// Jackson deserializer for {@link CurrencyCode}.
///
/// Reads a JSON string token and passes it to the `CurrencyCode` constructor,
/// which normalizes to uppercase and validates the ISO 4217 shape.
class CurrencyCodeDeserializer extends StdDeserializer<CurrencyCode> {

    CurrencyCodeDeserializer() {
        super(CurrencyCode.class);
    }

    @Override
    public CurrencyCode deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return new CurrencyCode(p.getText());
    }
}
