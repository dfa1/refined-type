package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.dfa1.refinedtype.*;

/// Jackson {@link SimpleModule} that wires serializers and deserializers for
/// all refined types — no per-type registration needed.
///
/// Register once with an `ObjectMapper`:
///
/// ```java
/// ObjectMapper mapper = new ObjectMapper()
///         .registerModule(new RefinedTypeModule());
/// ```
///
/// **Serializers** are shared per marker interface: one `RefinedIntSerializer`
/// covers every `RefinedInt` subtype, and so on for the other five interfaces.
/// Each serializer writes the raw `value()` — no wrapping object, no `"value"` key.
///
/// **Deserializers** are auto-discovered via reflection: {@link RefinedTypeDeserializers}
/// finds the single-arg constructor of any `Refined*` subtype and delegates to it,
/// so the constructor's validation runs at deserialization time.
@SuppressWarnings("rawtypes")
public class RefinedTypeModule extends SimpleModule {

    public RefinedTypeModule() {
        super("RefinedTypeModule");

        addSerializer(RefinedString.class, new RefinedStringSerializer());
        addSerializer(RefinedInt.class, new RefinedIntSerializer());
        addSerializer(RefinedShort.class, new RefinedShortSerializer());
        addSerializer(RefinedLong.class, new RefinedLongSerializer());
        addSerializer(RefinedFloat.class, new RefinedFloatSerializer());
        addSerializer(RefinedDouble.class, new RefinedDoubleSerializer());
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addDeserializers(new RefinedTypeDeserializers());
    }
}
