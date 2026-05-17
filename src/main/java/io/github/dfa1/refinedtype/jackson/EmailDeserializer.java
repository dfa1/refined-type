package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.dfa1.refinedtype.examples.Email;

import java.io.IOException;

/// Jackson deserializer for {@link Email}.
///
/// Reads a JSON string token and passes it to the `Email` constructor,
/// which enforces the validation invariant. An invalid value causes
/// `IllegalArgumentException` to propagate wrapped in a Jackson exception.
class EmailDeserializer extends StdDeserializer<Email> {

    EmailDeserializer() {
        super(Email.class);
    }

    @Override
    public Email deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        return new Email(p.getText());
    }
}
