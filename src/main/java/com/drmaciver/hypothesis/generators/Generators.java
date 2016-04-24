package com.drmaciver.hypothesis.generators;

/**
 * Created by david on 4/18/16.
 */
public interface Generators {
    static <T> ListGenerator<T> lists(DataGenerator<T> elementGenerator) {
        return new ListGenerator<T>(elementGenerator);
    }

    static IntegerGenerator integers() {
        return new IntegerGenerator();
    }
}
