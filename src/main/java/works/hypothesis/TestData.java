package works.hypothesis;

import works.hypothesis.strategies.Strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by david on 4/9/16.
 */
public abstract class TestData implements Comparable<TestData> {
    final List<Integer> intervalStarts = new ArrayList<Integer>();
    final List<Interval> intervals = new ArrayList<Interval>();
    byte[] record;
    boolean frozen = false;
    int index = 0;
    Status status = Status.VALID;
    int checksum = -1;

    public TestData(int maxSize) {
        this.record = new byte[maxSize];
    }

    @Override
    public String toString() {
        return "TestData{" +
                "record=" + Arrays.toString(record) +
                ", frozen=" + frozen +
                ", index=" + index +
                '}';
    }

    public int compareTo(TestData other) {
        if (!(frozen && other.frozen)) {
            throw new RuntimeException("Cannot compare non frozen TestDataForBuffer");
        }
        if (intervals.size() < other.intervals.size())
            return -1;
        if (intervals.size() > other.intervals.size())
            return 1;
        if (this.index < other.index)
            return -1;
        if (this.index > other.index)
            return 1;
        for (int i = 0; i < index; i++) {
            final int c = ByteUtils.unsigned(record[i]);
            final int d = ByteUtils.unsigned(other.record[i]);
            if (c < d)
                return -1;
            if (c > d)
                return 1;
        }
        return 0;
    }

    private void assertNotFrozen(String name) {
        if (frozen) {
            throw new Frozen("Cannot call " + name + " on a frozen TestDataForBuffer.");
        }
    }

    public <T> T draw(Strategy<T> generator) {
        startExample();
        final T result = generator.doDraw(this);
        stopExample();
        return result;
    }

    public byte drawByte() {
        return drawBytes(1)[0];
    }

    public byte[] drawBytes(int n) {
        return this.drawBytes(n, HypothesisUniformDataDistributions.INSTANCE);
    }

    public byte[] drawBytes(int n, HypothesisDataDistribution distribution) {
        assertNotFrozen("drawBytes");
        startExample();
        if (index + n > record.length) {
            status = Status.OVERRUN;
            freeze();
            throw new StopTest();
        }
        final byte[] result = doDrawBytes(n, distribution);
        System.arraycopy(result, 0, record, index, n);
        index += n;
        stopExample();
        return result;
    }

    void checkIntegrity() {
        if (!frozen) return;
        final int hashCode = Arrays.hashCode(record);
        if (hashCode != checksum) {
            throw new RuntimeException("TestData.record has been modified.");
        }
    }

    protected abstract byte[] doDrawBytes(int n, HypothesisDataDistribution distribution);

    public void freeze() {
        if (frozen)
            return;
        frozen = true;
        record = Arrays.copyOfRange(record, 0, index);
        checksum = Arrays.hashCode(record);
        Collections.sort(intervals);
    }

    public Status getStatus() {
        return status;
    }

    public int index() {
        return index;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void markInteresting() {
        assertNotFrozen("markInteresting");
        if (status == Status.VALID) {
            status = Status.INTERESTING;
        }
        throw new StopTest();
    }

    public void markInvalid() {
        assertNotFrozen("markInvalid");
        if (status == Status.VALID) {
            status = Status.INVALID;
        }
        throw new StopTest();
    }

    public void assume(boolean condition) {
        if (!condition) this.markInvalid();
    }

    public void startExample() {
        assertNotFrozen("startExample");
        intervalStarts.add(index);
    }

    public void stopExample() {
        assertNotFrozen("stopExample");
        final int k = intervalStarts.remove(intervalStarts.size() - 1);
        if (k != index) {
            final Interval interval = new Interval(k, index);
            if (intervals.size() == 0 || intervals.get(intervals.size() - 1) != interval)
                intervals.add(interval);
        }
    }
}
