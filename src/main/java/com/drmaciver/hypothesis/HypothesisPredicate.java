package com.drmaciver.hypothesis;

public interface HypothesisPredicate<T> {
	public boolean test(T value);
}
