package io.github.dfa1.refinedtypes.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.dfa1.refinedtypes.RefinedInt;

import java.io.IOException;

/// Jackson serializer for any {@link RefinedInt} implementation.
///
/// Writes the validated `value()` as a JSON NUMBER token. One serializer
/// covers all int-refined types through the marker interface.
@SuppressWarnings("rawtypes")
class RefinedIntSerializer extends StdSerializer<RefinedInt> {

    RefinedIntSerializer() {
        super(RefinedInt.class);
    }

    @Override
    public void serialize(RefinedInt value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeNumber(value.value());
    }
}
