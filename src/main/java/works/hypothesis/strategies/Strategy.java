package works.hypothesis.strategies;

import works.hypothesis.TestData;

@FunctionalInterface
public interface Strategy<T> {
    T doDraw(TestData data);
}
