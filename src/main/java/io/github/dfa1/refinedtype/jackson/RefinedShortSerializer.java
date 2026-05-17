package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.dfa1.refinedtype.RefinedShort;

import java.io.IOException;

/// Jackson serializer for any {@link RefinedShort} implementation.
///
/// Writes the validated `value()` as a JSON NUMBER token. One serializer
/// covers all short-refined types through the marker interface.
@SuppressWarnings("rawtypes")
class RefinedShortSerializer extends StdSerializer<RefinedShort> {

    RefinedShortSerializer() {
        super(RefinedShort.class);
    }

    @Override
    public void serialize(RefinedShort value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeNumber(value.value());
    }
}
