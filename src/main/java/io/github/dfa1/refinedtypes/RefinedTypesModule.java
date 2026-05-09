package io.github.dfa1.refinedtypes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.Serializers;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class RefinedTypesModule extends SimpleModule {

    private final RefinedTypeCache cache;

    public RefinedTypesModule() {
        this(RefinedTypeCache.NO_OP);
    }

    public RefinedTypesModule(RefinedTypeCache cache) {
        super("RefinedTypesModule");
        this.cache = cache;
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addSerializers(new RefinedSerializers());
        context.addDeserializers(new RefinedDeserializers(cache));
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
        private final RefinedTypeCache cache;

        RefinedDeserializers(RefinedTypeCache cache) {
            this.cache = cache;
        }

        @Override
        public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) {
            Class<?> raw = type.getRawClass();
            if (RefinedString.class.isAssignableFrom(raw)) {
                return new StringDeserializer<>(raw.asSubclass(RefinedString.class), cache);
            }
            if (RefinedInt.class.isAssignableFrom(raw)) {
                return new IntDeserializer<>(raw.asSubclass(RefinedInt.class), cache);
            }
            return null;
        }
    }

    private static class StringDeserializer<T extends RefinedString> extends JsonDeserializer<T> {
        private final Class<T> type;
        private final MethodHandle ctor;
        private final boolean lowCardinality;
        private final RefinedTypeCache cache;

        StringDeserializer(Class<T> type, RefinedTypeCache cache) {
            this.type = type;
            this.cache = cache;
            this.lowCardinality = LowCardinality.class.isAssignableFrom(type);
            try {
                MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(type, MethodHandles.lookup());
                ctor = lookup.findConstructor(type, MethodType.methodType(void.class, String.class));
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(type.getName() + " must have a (String) constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("cannot access constructor of " + type.getName(), e);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String raw = p.getText();
            try {
                if (lowCardinality) {
                    return cache.get(type, raw, () -> construct(raw));
                }
                return construct(raw);
            } catch (IllegalArgumentException e) {
                throw InvalidFormatException.from(p, e.getMessage(), raw, type);
            }
        }

        @SuppressWarnings("unchecked")
        private T construct(String raw) {
            try {
                return (T) ctor.invoke(raw);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class IntDeserializer<T extends RefinedInt> extends JsonDeserializer<T> {
        private final Class<T> type;
        private final MethodHandle ctor;
        private final boolean lowCardinality;
        private final RefinedTypeCache cache;

        IntDeserializer(Class<T> type, RefinedTypeCache cache) {
            this.type = type;
            this.cache = cache;
            this.lowCardinality = LowCardinality.class.isAssignableFrom(type);
            try {
                MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(type, MethodHandles.lookup());
                ctor = lookup.findConstructor(type, MethodType.methodType(void.class, int.class));
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(type.getName() + " must have a (int) constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("cannot access constructor of " + type.getName(), e);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            int raw = p.getIntValue();
            try {
                if (lowCardinality) {
                    return cache.get(type, raw, () -> construct(raw));
                }
                return construct(raw);
            } catch (IllegalArgumentException e) {
                throw InvalidFormatException.from(p, e.getMessage(), raw, type);
            }
        }

        @SuppressWarnings("unchecked")
        private T construct(int raw) {
            try {
                return (T) ctor.invoke(raw);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
