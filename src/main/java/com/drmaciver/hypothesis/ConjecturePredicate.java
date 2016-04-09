package com.drmaciver.hypothesis;

public interface ConjecturePredicate<T> {
	public boolean test(T value);
}
