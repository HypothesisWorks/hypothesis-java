package works.hypothesis.strategies;

import works.hypothesis.TestData;

import java.util.ArrayList;
import java.util.List;

public class ListStrategy<T> implements Strategy<List<T>> {

    private final Strategy<T> elementGenerator;

    ListStrategy(Strategy<T> elementGenerator) {
        this.elementGenerator = elementGenerator;
    }

    public List<T> doDraw(TestData data) {
        final List<T> result = new ArrayList<T>();
        while (true) {
            data.startExample();
            if (data.drawByte() <= 50) {
                data.stopExample();
                break;
            }
            result.add(this.elementGenerator.doDraw(data));
            data.stopExample();
        }
        return result;
    }
}
