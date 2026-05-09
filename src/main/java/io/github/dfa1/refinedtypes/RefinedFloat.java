package io.github.dfa1.refinedtypes;

public interface RefinedFloat extends Comparable<RefinedFloat> {

    float value();

    @Override
    default int compareTo(RefinedFloat that) {
        return Float.compare(this.value(), that.value());
    }
}
