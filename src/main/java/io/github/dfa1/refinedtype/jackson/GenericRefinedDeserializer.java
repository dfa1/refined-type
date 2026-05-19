package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/// Generic deserializer for any refined type.
///
/// Reads a single JSON token via `reader`, passes the value to the type's
/// static `of(T)` factory method, and lets the factory validate the constraint.
/// `IllegalArgumentException` from the factory is re-thrown as-is so
/// callers see the domain validation message without Jackson wrapping.
class GenericRefinedDeserializer<T> extends StdDeserializer<T> {

    @FunctionalInterface
    interface TokenReader {
        Object read(JsonParser p) throws IOException;
    }

    private final Method factory;
    private final TokenReader reader;

    @SuppressWarnings("unchecked")
    GenericRefinedDeserializer(Method factory, TokenReader reader) {
        super(factory.getDeclaringClass());
        this.factory = factory;
        this.reader = reader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        try {
            return (T) factory.invoke(null, reader.read(p));
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalArgumentException iae) throw iae;
            throw new IOException("Failed to deserialize " + handledType().getSimpleName(), e);
        } catch (ReflectiveOperationException e) {
            throw new IOException("Failed to deserialize " + handledType().getSimpleName(), e);
        }
    }
}
