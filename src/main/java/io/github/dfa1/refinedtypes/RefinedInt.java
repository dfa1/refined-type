package io.github.dfa1.refinedtypes;

public interface RefinedInt extends Comparable<RefinedInt> {

	int value();

	@Override
	default int compareTo(RefinedInt that) {
		return Integer.compare(this.value(), that.value());
	}

}
