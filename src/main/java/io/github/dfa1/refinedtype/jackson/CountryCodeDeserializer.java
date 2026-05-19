package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.dfa1.refinedtype.examples.CountryCode;

import java.io.IOException;

/// Jackson deserializer for {@link CountryCode}.
///
/// Reads a JSON string token and passes it to the `CountryCode` constructor,
/// which normalizes to uppercase and validates the ISO 3166-1 alpha-2 format.
class CountryCodeDeserializer extends StdDeserializer<CountryCode> {

    CountryCodeDeserializer() {
        super(CountryCode.class);
    }

    @Override
    public CountryCode deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return CountryCode.of(p.getText());
    }
}
