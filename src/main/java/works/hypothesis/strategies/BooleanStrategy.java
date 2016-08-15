package works.hypothesis.strategies;

import works.hypothesis.TestData;

/**
 * Created by david on 15/08/2016.
 */
public class BooleanStrategy implements Strategy<Boolean> {
    @Override
    public Boolean doDraw(TestData data) {
        return (data.drawByte() & 1) != 0;
    }
}
