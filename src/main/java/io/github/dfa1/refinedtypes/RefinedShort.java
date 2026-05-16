package io.github.dfa1.refinedtypes;

public interface RefinedShort extends Comparable<RefinedShort> {

    short value();

    @Override
    default int compareTo(RefinedShort that) {
        return Short.compare(this.value(), that.value());
    }
}
