package com.drmaciver.hypothesis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.drmaciver.hypothesis.TestData.Interval;
import com.drmaciver.hypothesis.TestData.Status;
import com.drmaciver.hypothesis.TestData.StopTest;

class TestRunner {
	static class StopShrinking extends RuntimeException {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

	}

	static byte[] findInterestingBuffer(ConjectureTestFunction test, ConjectureSettings settings) {
		final TestRunner runner = new TestRunner(test, settings);
		runner.run();
		if (runner.lastData.getStatus() == Status.INTERESTING) {
			runner.lastData.checkIntegrity();
			System.out.println(Arrays.toString(runner.lastData.buffer));
			return runner.lastData.buffer;
		}
		return null;
	}

	final Random random;
	ConjectureTestFunction _testFunction;
	ConjectureSettings settings;
	int changed = 0;
	int shrinks = 0;

	int validExamples = 0;

	TestData lastData = null;

	public TestRunner(ConjectureTestFunction _testFunction, ConjectureSettings settings) {
		super();
		this._testFunction = _testFunction;
		if (settings == null)
			settings = new ConjectureSettings();
		this.settings = settings;
		random = new Random();
	}

	private void _run() {
		newBuffer();
		int mutations = 0;
		int generation = 0;
		while (validExamples <= settings.getMaxExamples() && lastData.getStatus() != Status.INTERESTING) {
			if (mutations >= settings.getMutations()) {
				generation += 1;
				mutations = 0;
				incorporateNewBuffer(mutateDataToNewBuffer());
			} else {
				newBuffer();
			}
		}
		if (lastData.getStatus() != Status.INTERESTING) {
			return;
		}

		// We have successfully found an interesting buffer and shrinking starts
		// here

		int changeCounter = -1;
		while (changed > changeCounter) {
			changeCounter = changed;
			assert lastData.getStatus() == Status.INTERESTING;
			for (int i = 0; i < lastData.intervals.size();) {
				final Interval interval = lastData.intervals.get(i);
				if (!incorporateNewBuffer(ByteUtils.deleteInterval(lastData.buffer, interval.start, interval.end))) {
					i++;
				}
			}
			if (changed > changeCounter)
				continue;
			for (int i = 0; i < lastData.intervals.size(); i++) {
				final Interval interval = lastData.intervals.get(i);
				incorporateNewBuffer(ByteUtils.zeroInterval(lastData.buffer, interval.start, interval.end));
			}
			for (int i = 0; i < lastData.intervals.size(); i++) {
				final Interval interval = lastData.intervals.get(i);
				incorporateNewBuffer(ByteUtils.sortInterval(lastData.buffer, interval.start, interval.end));
			}
			if (changed > changeCounter)
				continue;

			for (int i = 0; i < lastData.buffer.length - 8; i++) {
				incorporateNewBuffer(ByteUtils.zeroInterval(lastData.buffer, i, i + 8));
			}
			for (int i = 0; i < lastData.buffer.length; i++) {
				if (lastData.buffer[i] == 0)
					continue;
				final byte[] buf = lastData.buffer.clone();
				buf[i]--;
				if (!incorporateNewBuffer(buf))
					break;
				for (int c = 0; c < ByteUtils.unsigned(lastData.buffer[i]) - 1; c++) {
					buf[i] = (byte) c;
					if (incorporateNewBuffer(buf))
						break;
				}
			}

			for (int i = 0; i < lastData.buffer.length - 1; i++) {
				incorporateNewBuffer(ByteUtils.sortInterval(lastData.buffer, i, i + 2));
			}
			if (changed > changeCounter)
				continue;
			for (int i = 0; i < lastData.buffer.length; i++) {
				incorporateNewBuffer(ByteUtils.deleteInterval(lastData.buffer, i, i + 1));
			}
			for (int i = 0; i < lastData.buffer.length; i++) {
				if (lastData.buffer[i] == 0) {
					final byte[] buf = lastData.buffer.clone();
					for (int j = i; j >= 0; j--) {
						if (buf[j] != 0) {
							buf[j]--;
							incorporateNewBuffer(buf);
							break;
						} else {
							buf[j] = (byte) 255;
						}
					}
				}
			}
			if (changed > changeCounter)
				continue;
			final List<List<Integer>> buckets = new ArrayList<List<Integer>>();
			for (int i = 0; i < 256; i++)
				buckets.add(new ArrayList<Integer>());
			for (int i = 0; i < lastData.buffer.length; i++) {
				buckets.get(ByteUtils.unsigned(lastData.buffer[i])).add(i);
			}
			for (final List<Integer> bucket : buckets) {
				for (final int j : bucket) {
					for (final int k : bucket) {
						if (j < k) {
							final byte[] buf = lastData.buffer.clone();
							if (buf[j] == buf[k]) {
								if (buf[j] == 0) {
									if (j > 0 && buf[j - 1] != 0 && buf[k - 1] != 0) {
										buf[j - 1]--;
										buf[k - 1]--;
										buf[j] = (byte) 255;
										buf[k] = (byte) 255;
										incorporateNewBuffer(buf);
										continue;
									}
								} else {
									for (byte c = 0; c < buf[j]; c++) {
										buf[j] = c;
										buf[k] = c;
										if (incorporateNewBuffer(buf))
											break;
									}
								}
							}
						}
					}
				}
			}
			if (changed > changeCounter)
				continue;
			for (int i = 0; i < lastData.buffer.length; i++) {
				if (lastData.buffer[i] == 0)
					continue;
				for (int j = i + 1; j < lastData.buffer.length; j++) {
					if (ByteUtils.unsigned(lastData.buffer[i]) > ByteUtils.unsigned(lastData.buffer[j])) {
						incorporateNewBuffer(ByteUtils.swap(lastData.buffer, i, j));
					}
					if (lastData.buffer[i] != 0 && lastData.buffer[j] != 0) {
						final byte[] buf = lastData.buffer.clone();
						buf[i]--;
						buf[j]--;
						incorporateNewBuffer(buf);
					}
				}
			}
		}
	}

	boolean considerNewTestData(TestData data) {
		if (lastData.getStatus().compareTo(data.getStatus()) < 0) {
			return true;
		}
		if (lastData.getStatus().compareTo(data.getStatus()) > 0) {
			return false;
		}
		if (data.getStatus() == Status.INVALID) {
			return data.index() >= lastData.index();
		}
		if (data.getStatus() == Status.OVERRUN) {
			return data.index() <= lastData.index();
		}
		if (data.getStatus() == Status.INTERESTING) {
			return lastData.compareTo(data) > 0;
		}
		return true;
	}

	boolean incorporateNewBuffer(byte[] buffer) {
		assert buffer.length <= settings.getBufferSize();
		if (Arrays.equals(buffer, lastData.buffer))
			return false;
		final TestData data = new TestData(buffer);
		testFunction(data);
		if (considerNewTestData(data)) {
			if (lastData.getStatus() == Status.INTERESTING) {
				shrinks++;
			}
			changed++;
			lastData = data;
			if (settings.isDebug())
				System.out.println(Arrays.toString(Arrays.copyOfRange(data.buffer, 0, data.index)));
			if (shrinks >= settings.getMaxShrinks())
				throw new StopShrinking();
			return true;
		}
		return false;
	}

	private byte[] mutateDataToNewBuffer() {
		final int n = Math.min(lastData.buffer.length, lastData.index());
		if (n == 0) {
			return new byte[0];
		}
		if (n == 1) {
			final byte[] result = new byte[1];
			random.nextBytes(result);
			return result;
		}
		final byte[] result = lastData.buffer.clone();
		if (lastData.getStatus() == Status.OVERRUN) {
			for (int i = 0; i < result.length; i++) {
				if (result[i] == 0)
					continue;
				switch (random.nextInt(3)) {
				case 0:
					result[i] = 0;
					break;
				case 1:
					result[i] = (byte) random.nextInt(ByteUtils.unsigned(result[i]));
					break;
				case 2:
					continue;
				}
			}
			return result;
		}
		if (lastData.intervals.size() <= 1 || random.nextInt(3) == 0) {
			int u, v;
			if (random.nextBoolean() || lastData.intervals.size() <= 1) {
				u = random.nextInt(lastData.buffer.length);
				v = u + random.nextInt(lastData.buffer.length - u);
			} else {
				final Interval in = lastData.intervals.get(random.nextInt(lastData.intervals.size()));
				u = in.start;
				v = in.end;
			}
			switch (random.nextInt(3)) {
			case 0:
				for (int i = u; i < v; i++) {
					result[i] = 0;
				}
				break;
			case 1:
				for (int i = u; i < v; i++) {
					result[i] = (byte) 255;
				}
				break;
			case 2:
				for (int i = u; i < v; i++) {
					result[i] = (byte) random.nextInt(256);
				}
				break;
			}
		} else {
			final int i = random.nextInt(lastData.intervals.size() - 1);
			final int j = i + 1 + random.nextInt(lastData.intervals.size() - 1 - i);
			final Interval int1 = lastData.intervals.get(i);
			final Interval int2 = lastData.intervals.get(j);
			assert int2.length() <= int1.length();
			System.arraycopy(lastData.buffer, int2.start, result, int1.start, int2.length());
			if (int1.length() != int2.length()) {
				System.arraycopy(lastData.buffer, int1.end, result, int1.start + int2.length(),
						lastData.buffer.length - int1.end);
			}
		}
		return result;
	}

	void newBuffer() {
		final byte[] buffer = new byte[settings.getBufferSize()];
		random.nextBytes(buffer);
		final TestData data = new TestData(buffer);
		testFunction(data);
		data.freeze();
		lastData = data;
	}

	void run() {
		try {
			_run();
		} catch (final StopShrinking e) {

		}
	}

	private void testFunction(TestData data) {
		try {
			_testFunction.runTest(data);
		} catch (final StopTest st) {
		}
		data.freeze();
		if (data.status.compareTo(Status.VALID) >= 0)
			validExamples++;
	}
}
