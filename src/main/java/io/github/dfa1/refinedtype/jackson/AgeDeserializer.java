package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.dfa1.refinedtype.examples.Age;

import java.io.IOException;

/// Jackson deserializer for {@link Age}.
///
/// Reads a JSON NUMBER token and passes the `int` value to the `Age`
/// constructor, which validates the `[0, 150]` range.
class AgeDeserializer extends StdDeserializer<Age> {

    AgeDeserializer() {
        super(Age.class);
    }

    @Override
    public Age deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return new Age(p.getIntValue());
    }
}
