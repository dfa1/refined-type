package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import io.github.dfa1.refinedtype.*;

import java.lang.reflect.Constructor;

/// Auto-discovers and creates deserializers for any {@link RefinedInt},
/// {@link RefinedShort}, {@link RefinedLong}, {@link RefinedFloat},
/// {@link RefinedDouble}, or {@link RefinedString} subtype.
///
/// Uses reflection to find the single-arg constructor matching the marker
/// interface's primitive type and wraps it in a {@link GenericRefinedDeserializer}.
/// No per-type registration needed — any new refined type is handled automatically.
class RefinedTypeDeserializers extends Deserializers.Base {

    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type,
            DeserializationConfig config, BeanDescription beanDesc) {
        Class<?> raw = type.getRawClass();

        if (RefinedString.class.isAssignableFrom(raw)) {
            return deserializer(raw, String.class, p -> p.getText());
        }
        if (RefinedDouble.class.isAssignableFrom(raw)) {
            return deserializer(raw, double.class, p -> p.getDoubleValue());
        }
        if (RefinedFloat.class.isAssignableFrom(raw)) {
            return deserializer(raw, float.class, p -> p.getFloatValue());
        }
        if (RefinedLong.class.isAssignableFrom(raw)) {
            return deserializer(raw, long.class, p -> p.getLongValue());
        }
        if (RefinedInt.class.isAssignableFrom(raw)) {
            return deserializer(raw, int.class, p -> p.getIntValue());
        }
        if (RefinedShort.class.isAssignableFrom(raw)) {
            // Some types use int constructor (e.g. Age), others use short (e.g. AudioSample)
            Constructor<?> ctor = findCtor(raw, int.class);
            if (ctor != null) return new GenericRefinedDeserializer<>(ctor, p -> p.getIntValue());
            ctor = findCtor(raw, short.class);
            if (ctor != null) return new GenericRefinedDeserializer<>(ctor, p -> p.getShortValue());
        }

        return null;
    }

    private static GenericRefinedDeserializer<?> deserializer(Class<?> type, Class<?> ctorParam,
            GenericRefinedDeserializer.TokenReader reader) {
        Constructor<?> ctor = findCtor(type, ctorParam);
        if (ctor == null) {
            throw new IllegalStateException(
                    type.getSimpleName() + " has no (" + ctorParam.getSimpleName() + ") constructor");
        }
        return new GenericRefinedDeserializer<>(ctor, reader);
    }

    private static Constructor<?> findCtor(Class<?> type, Class<?>... params) {
        try {
            return type.getConstructor(params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
