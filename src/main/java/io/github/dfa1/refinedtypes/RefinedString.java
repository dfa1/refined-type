package io.github.dfa1.refinedtypes;

public interface RefinedString extends Comparable<RefinedString> {

    String value();

    @Override
    default int compareTo(RefinedString that) {
        return this.value().compareTo(that.value());
    }
}
