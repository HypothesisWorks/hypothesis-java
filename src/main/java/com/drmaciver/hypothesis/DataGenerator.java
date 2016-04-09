package com.drmaciver.hypothesis;

@FunctionalInterface
public interface DataGenerator<T> {
	public T doDraw(TestData data);
}
