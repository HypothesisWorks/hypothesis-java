package works.hypothesis.strategies;

import works.hypothesis.ByteUtils;
import works.hypothesis.TestData;

public class IntegerStrategy implements Strategy<Integer> {
    IntegerStrategy() {
    }

    public Integer doDraw(TestData data) {
        return ByteUtils.intFromBytes(data.drawBytes(4), 0);
    }
}