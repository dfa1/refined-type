package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.dfa1.refinedtype.RefinedLong;

import java.io.IOException;

/// Jackson serializer for any {@link RefinedLong} implementation.
///
/// Writes the validated `value()` as a JSON number token. One serializer
/// covers all long-refined types through the marker interface.
@SuppressWarnings("rawtypes")
class RefinedLongSerializer extends StdSerializer<RefinedLong> {

    RefinedLongSerializer() {
        super(RefinedLong.class);
    }

    @Override
    public void serialize(RefinedLong value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeNumber(value.value());
    }
}
