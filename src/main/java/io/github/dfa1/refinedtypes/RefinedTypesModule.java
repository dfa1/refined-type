package io.github.dfa1.refinedtypes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.Serializers;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RefinedTypesModule extends SimpleModule {

    public RefinedTypesModule() {
        super("RefinedTypesModule");
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addSerializers(new RefinedSerializers());
        context.addDeserializers(new RefinedDeserializers());
    }

    // ── serializers ──────────────────────────────────────────────────────────

    private static class RefinedSerializers extends Serializers.Base {
        @Override
        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
            Class<?> raw = type.getRawClass();
            if (RefinedString.class.isAssignableFrom(raw)) return StringSerializer.INSTANCE;
            if (RefinedInt.class.isAssignableFrom(raw))    return IntSerializer.INSTANCE;
            return null;
        }
    }

    private static class StringSerializer extends JsonSerializer<RefinedString> {
        static final StringSerializer INSTANCE = new StringSerializer();

        @Override
        public void serialize(RefinedString value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.value());
        }
    }

    private static class IntSerializer extends JsonSerializer<RefinedInt> {
        static final IntSerializer INSTANCE = new IntSerializer();

        @Override
        public void serialize(RefinedInt value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeNumber(value.value());
        }
    }

    // ── deserializers ────────────────────────────────────────────────────────

    private static class RefinedDeserializers extends Deserializers.Base {
        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) {
            Class<?> raw = type.getRawClass();
            if (RefinedString.class.isAssignableFrom(raw)) {
                return new StringDeserializer<>(raw.asSubclass(RefinedString.class));
            }
            if (RefinedInt.class.isAssignableFrom(raw)) {
                return new IntDeserializer<>(raw.asSubclass(RefinedInt.class));
            }
            return null;
        }
    }

    private static class StringDeserializer<T extends RefinedString> extends JsonDeserializer<T> {
        private final Constructor<T> ctor;

        StringDeserializer(Class<T> type) {
            try {
                ctor = type.getDeclaredConstructor(String.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(type.getName() + " must have a (String) constructor", e);
            }
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String raw = p.getText();
            try {
                return ctor.newInstance(raw);
            } catch (InvocationTargetException e) {
                throw InvalidFormatException.from(p, e.getCause().getMessage(), raw, ctor.getDeclaringClass());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class IntDeserializer<T extends RefinedInt> extends JsonDeserializer<T> {
        private final Constructor<T> ctor;

        IntDeserializer(Class<T> type) {
            try {
                ctor = type.getDeclaredConstructor(int.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(type.getName() + " must have a (int) constructor", e);
            }
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            int raw = p.getIntValue();
            try {
                return ctor.newInstance(raw);
            } catch (InvocationTargetException e) {
                throw InvalidFormatException.from(p, e.getCause().getMessage(), raw, ctor.getDeclaringClass());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
