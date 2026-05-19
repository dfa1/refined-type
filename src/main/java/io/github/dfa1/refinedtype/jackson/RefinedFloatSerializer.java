package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.dfa1.refinedtype.RefinedFloat;

import java.io.IOException;

/// Jackson serializer for any {@link RefinedFloat} implementation.
///
/// Writes the validated `value()` as a JSON number token. One serializer
/// covers all float-refined types through the marker interface.
@SuppressWarnings("rawtypes")
class RefinedFloatSerializer extends StdSerializer<RefinedFloat> {

    RefinedFloatSerializer() {
        super(RefinedFloat.class);
    }

    @Override
    public void serialize(RefinedFloat value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeNumber(value.value());
    }
}
