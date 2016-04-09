package com.drmaciver.hypothesis;

@FunctionalInterface
public interface DataGenerator<T> {
	T doDraw(TestData data);
}
