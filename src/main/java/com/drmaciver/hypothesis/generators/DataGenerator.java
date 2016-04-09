package com.drmaciver.hypothesis.generators;

import com.drmaciver.hypothesis.TestData;

@FunctionalInterface
public interface DataGenerator<T> {
    T doDraw(TestData data);
}
