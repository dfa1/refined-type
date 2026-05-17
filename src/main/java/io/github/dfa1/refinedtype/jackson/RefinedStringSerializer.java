package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.dfa1.refinedtype.RefinedString;

import java.io.IOException;

/// Jackson serializer for any {@link RefinedString} implementation.
///
/// Writes the validated `value()` as a JSON string token. One serializer
/// covers all string-refined types through the marker interface.
@SuppressWarnings("rawtypes")
class RefinedStringSerializer extends StdSerializer<RefinedString> {

    RefinedStringSerializer() {
        super(RefinedString.class);
    }

    @Override
    public void serialize(RefinedString value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeString(value.value());
    }
}
