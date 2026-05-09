package io.github.dfa1.refinedtypes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ConcurrentMapCache implements RefinedTypeCache {

    private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<Object, Object>> store =
            new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type, Object key, Supplier<T> factory) {
        return (T) store
                .computeIfAbsent(type, k -> new ConcurrentHashMap<>())
                .computeIfAbsent(key, k -> factory.get());
    }

    public int size(Class<?> type) {
        ConcurrentHashMap<?, ?> bucket = store.get(type);
        return bucket == null ? 0 : bucket.size();
    }
}
