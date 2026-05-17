package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.dfa1.refinedtype.RefinedInt;
import io.github.dfa1.refinedtype.RefinedString;
import io.github.dfa1.refinedtype.examples.Age;
import io.github.dfa1.refinedtype.examples.CountryCode;
import io.github.dfa1.refinedtype.examples.CurrencyCode;
import io.github.dfa1.refinedtype.examples.Email;
import io.github.dfa1.refinedtype.examples.Port;

/// Jackson {@link SimpleModule} that registers serializers and deserializers
/// for the refined-type examples.
///
/// Register once with an `ObjectMapper`:
///
/// ```java
/// ObjectMapper mapper = new ObjectMapper()
///         .registerModule(new RefinedTypeModule());
/// ```
///
/// Serializers are shared via the marker interfaces (`RefinedString`,
/// `RefinedInt`). Deserializers are per concrete type because each
/// constructor is the only valid factory for its value class.
@SuppressWarnings("rawtypes")
public class RefinedTypeModule extends SimpleModule {

    public RefinedTypeModule() {
        super("RefinedTypeModule");

        addSerializer(RefinedString.class, new RefinedStringSerializer());
        addSerializer(RefinedInt.class, new RefinedIntSerializer());

        addDeserializer(Email.class, new EmailDeserializer());
        addDeserializer(CountryCode.class, new CountryCodeDeserializer());
        addDeserializer(CurrencyCode.class, new CurrencyCodeDeserializer());
        addDeserializer(Age.class, new AgeDeserializer());
        addDeserializer(Port.class, new PortDeserializer());
    }
}
