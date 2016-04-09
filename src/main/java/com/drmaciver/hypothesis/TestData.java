package com.drmaciver.hypothesis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestData implements Comparable<TestData> {
	static class Frozen extends HypothesisException {
		/**
		 * A mutation method has been called on a frozen TestData.
		 */
		private static final long serialVersionUID = 1L;

		public Frozen(String arg0) {
			super(arg0);
		}
	}

	static class Interval implements Comparable<Interval> {
		final int start;

		final int end;

		public Interval(int start, int end) {
			super();
			this.start = start;
			this.end = end;
		}

		public int compareTo(Interval other) {
			if (length() < other.length())
				return -1;
			if (length() > other.length())
				return 1;
			if (start < other.start)
				return -1;
			if (start > other.start)
				return 1;
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final Interval other = (Interval) obj;
			if (end != other.end)
				return false;
			if (start != other.start)
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + end;
			result = prime * result + start;
			return result;
		}

		int length() {
			return end - start;
		}
	}

	enum Status {
		OVERRUN, INVALID, VALID, INTERESTING;
	}

	static class StopTest extends HypothesisException {
		/**
		 * A condition that ends the current test has occurred.
		 */
		private static final long serialVersionUID = 1L;

	}

	byte[] buffer;

	int checksum;
	int index = 0;
	int _cost = 0;
	Status status = Status.VALID;
	boolean frozen = false;
	List<Integer> intervalStarts = new ArrayList<Integer>();
	List<Interval> intervals = new ArrayList<Interval>();

	public TestData(byte[] buffer) {
		this.buffer = buffer.clone();
		checksum = Arrays.hashCode(buffer);
	}

	private void assertNotFrozen(String name) {
		if (frozen) {
			throw new Frozen("Cannot call " + name + " on a frozen TestData.");
		}
	}

	void checkIntegrity() {
		if (Arrays.hashCode(buffer) != checksum) {
			throw new RuntimeException("TestData.buffer has been modified.");
		}
	}

	public int compareTo(TestData other) {
		if (!(frozen && other.frozen)) {
			throw new RuntimeException("Cannot compare non frozen TestData");
		}
		if (cost() < other.cost())
			return -1;
		if (cost() > other.cost())
			return 1;
		if (intervals.size() < other.intervals.size())
			return -1;
		if (intervals.size() > other.intervals.size())
			return 1;
		if (buffer.length < other.buffer.length)
			return -1;
		if (buffer.length > other.buffer.length)
			return 1;
		for (int i = 0; i < buffer.length; i++) {
			final int c = ByteUtils.unsigned(buffer[i]);
			final int d = ByteUtils.unsigned(other.buffer[i]);
			if (c < d)
				return -1;
			if (c > d)
				return 1;
		}
		return 0;
	}

	public int cost() {
		return _cost;
	}

	public <T> T draw(DataGenerator<T> generator) {
		startExample();
		final T result = generator.doDraw(this);
		stopExample();
		return result;
	}

	public byte drawByte() {
		return drawBytes(1)[0];
	}

	public byte[] drawBytes(int n) {
		assertNotFrozen("drawBytes");
		startExample();
		index += n;
		if (index > buffer.length) {
			status = Status.OVERRUN;
			freeze();
			throw new StopTest();
		}
		final byte[] result = doDrawBytes(n);
		stopExample();
		return result;
	}

	private byte[] doDrawBytes(int n) {
		return (byte[]) Arrays.copyOfRange(buffer, index - n, index);
	}

	public void freeze() {
		if (frozen)
			return;
		frozen = true;
		Collections.sort(intervals);
		if (status == Status.INTERESTING && buffer.length > index) {
			buffer = Arrays.copyOfRange(buffer, 0, index);
			checksum = Arrays.hashCode(buffer);
		}
	}

	public Status getStatus() {
		return status;
	}

	public void incurCost(int cost) {
		assertNotFrozen("incurCost");
		_cost += cost;
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
