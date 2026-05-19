package io.github.dfa1.refinedtype.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/// Generic deserializer for any refined type.
///
/// Reads a single JSON token via `reader`, passes the value to the type's
/// single-arg constructor, and lets the constructor validate the constraint.
/// `IllegalArgumentException` from the constructor is re-thrown as-is so
/// callers see the domain validation message without Jackson wrapping.
class GenericRefinedDeserializer<T> extends StdDeserializer<T> {

    @FunctionalInterface
    interface TokenReader {
        Object read(JsonParser p) throws IOException;
    }

    private final Constructor<T> ctor;
    private final TokenReader reader;

    @SuppressWarnings("unchecked")
    GenericRefinedDeserializer(Constructor<?> ctor, TokenReader reader) {
        super(ctor.getDeclaringClass());
        this.ctor = (Constructor<T>) ctor;
        this.reader = reader;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        try {
            return ctor.newInstance(reader.read(p));
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IllegalArgumentException iae) throw iae;
            throw new IOException("Failed to deserialize " + handledType().getSimpleName(), e);
        } catch (ReflectiveOperationException e) {
            throw new IOException("Failed to deserialize " + handledType().getSimpleName(), e);
        }
    }
}
