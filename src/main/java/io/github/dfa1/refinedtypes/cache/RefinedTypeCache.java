package io.github.dfa1.refinedtypes.cache;

import java.util.function.Supplier;

public interface RefinedTypeCache {

    <T> T get(Class<T> type, Object key, Supplier<T> factory);

    RefinedTypeCache NO_OP = new RefinedTypeCache() {
        @Override
        public <T> T get(Class<T> type, Object key, Supplier<T> factory) {
            return factory.get();
        }
    };
}
