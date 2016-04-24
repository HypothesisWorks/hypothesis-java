package com.drmaciver.hypothesis.generators;

import com.drmaciver.hypothesis.ByteUtils;
import com.drmaciver.hypothesis.TestData;

public class IntegerGenerator implements DataGenerator<Integer> {
    IntegerGenerator() {
    }

    public Integer doDraw(TestData data) {
        return ByteUtils.intFromBytes(data.drawBytes(4), 0);
    }
}