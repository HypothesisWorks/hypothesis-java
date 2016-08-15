package works.hypothesis;

import java.util.Arrays;

public class TestDataForBuffer extends TestData {

    private final byte[] buffer;

    public TestDataForBuffer(byte[] buffer) {
        super(buffer.length);
        this.buffer = buffer.clone();
    }

    @Override
    protected byte[] doDrawBytes(int n, HypothesisDataDistribution distribution) {
        return Arrays.copyOfRange(buffer, index(), index() + n);
    }

}
