package com.drmaciver.hypothesis;

public interface HypothesisPredicate<T> {
	boolean test(T value);
}
