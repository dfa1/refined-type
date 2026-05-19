package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.dfa1.refinedtype.RefinedDouble;

import java.io.IOException;

/// Jackson serializer for any {@link RefinedDouble} implementation.
///
/// Writes the validated `value()` as a JSON number token. One serializer
/// covers all double-refined types through the marker interface.
@SuppressWarnings("rawtypes")
class RefinedDoubleSerializer extends StdSerializer<RefinedDouble> {

    RefinedDoubleSerializer() {
        super(RefinedDouble.class);
    }

    @Override
    public void serialize(RefinedDouble value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeNumber(value.value());
    }
}
