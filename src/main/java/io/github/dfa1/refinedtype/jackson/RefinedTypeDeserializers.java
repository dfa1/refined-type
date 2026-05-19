package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import io.github.dfa1.refinedtype.*;

import java.lang.reflect.Method;

/// Auto-discovers and creates deserializers for any {@link RefinedInt},
/// {@link RefinedShort}, {@link RefinedLong}, {@link RefinedFloat},
/// {@link RefinedDouble}, or {@link RefinedString} subtype.
///
/// Uses reflection to find the static `of(T)` factory method matching the
/// marker interface's primitive type and wraps it in a {@link GenericRefinedDeserializer}.
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
            // Some types use int factory (e.g. Age), others use short (e.g. AudioSample)
            Method m = findFactory(raw, int.class);
            if (m != null) return new GenericRefinedDeserializer<>(m, p -> p.getIntValue());
            m = findFactory(raw, short.class);
            if (m != null) return new GenericRefinedDeserializer<>(m, p -> p.getShortValue());
        }

        return null;
    }

    private static GenericRefinedDeserializer<?> deserializer(Class<?> type, Class<?> param,
            GenericRefinedDeserializer.TokenReader reader) {
        Method m = findFactory(type, param);
        if (m == null) {
            throw new IllegalStateException(
                    type.getSimpleName() + " has no static of(" + param.getSimpleName() + ") factory");
        }
        return new GenericRefinedDeserializer<>(m, reader);
    }

    private static Method findFactory(Class<?> type, Class<?>... params) {
        try {
            return type.getMethod("of", params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
