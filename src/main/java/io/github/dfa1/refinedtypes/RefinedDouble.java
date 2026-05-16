package io.github.dfa1.refinedtypes;

public interface RefinedDouble extends Comparable<RefinedDouble> {

    double value();

    @Override
    default int compareTo(RefinedDouble that) {
        return Double.compare(this.value(), that.value());
    }
}
