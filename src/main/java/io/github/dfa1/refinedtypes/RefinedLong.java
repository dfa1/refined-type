package io.github.dfa1.refinedtypes;

public interface RefinedLong extends Comparable<RefinedLong> {

    long value();

    @Override
    default int compareTo(RefinedLong that) {
        return Long.compare(this.value(), that.value());
    }
}
