package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.dfa1.refinedtype.examples.Port;

import java.io.IOException;

/// Jackson deserializer for {@link Port}.
///
/// Reads a JSON NUMBER token and passes the `int` value to the `Port`
/// constructor, which validates the `[0, 65535]` range.
class PortDeserializer extends StdDeserializer<Port> {

    PortDeserializer() {
        super(Port.class);
    }

    @Override
    public Port deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return Port.of(p.getIntValue());
    }
}
