package com.drmaciver.hypothesis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class TestRunner {
	final Random random;
	final HypothesisTestFunction _testFunction;
	final HypothesisSettings settings;
	int changed = 0;
	int shrinks = 0;
	int validExamples = 0;
	TestData lastData = null;

	public TestRunner(HypothesisTestFunction _testFunction, HypothesisSettings settings) {
		super();
		this._testFunction = _testFunction;
		if (settings == null)
			settings = new HypothesisSettings();
		this.settings = settings;
		random = new Random();
	}

    static byte[] findInterestingBuffer(HypothesisTestFunction test, HypothesisSettings settings) {
        final TestRunner runner = new TestRunner(test, settings);
        runner.run();
        if (runner.lastData.getStatus() == Status.INTERESTING) {
            runner.lastData.checkIntegrity();
            return runner.lastData.record;
        }
        return null;
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
                if (!incorporateNewBuffer(ByteUtils.deleteInterval(lastData.record, interval.start, interval.end))) {
                    i++;
				}
			}
			if (changed > changeCounter)
				continue;
			for (int i = 0; i < lastData.intervals.size(); i++) {
				final Interval interval = lastData.intervals.get(i);
                incorporateNewBuffer(ByteUtils.zeroInterval(lastData.record, interval.start, interval.end));
            }
			for (int i = 0; i < lastData.intervals.size(); i++) {
				final Interval interval = lastData.intervals.get(i);
                incorporateNewBuffer(ByteUtils.sortInterval(lastData.record, interval.start, interval.end));
            }
			if (changed > changeCounter)
				continue;

            for (int i = 0; i < lastData.record.length - 8; i++) {
                incorporateNewBuffer(ByteUtils.zeroInterval(lastData.record, i, i + 8));
            }
            for (int i = 0; i < lastData.record.length; i++) {
                if (lastData.record[i] == 0)
                    continue;
                final byte[] buf = lastData.record.clone();
                buf[i]--;
				if (!incorporateNewBuffer(buf))
					break;
                for (int c = 0; c < ByteUtils.unsigned(lastData.record[i]) - 1; c++) {
                    buf[i] = (byte) c;
					if (incorporateNewBuffer(buf))
						break;
				}
			}

            for (int i = 0; i < lastData.record.length - 1; i++) {
                incorporateNewBuffer(ByteUtils.sortInterval(lastData.record, i, i + 2));
            }
			if (changed > changeCounter)
				continue;
            for (int i = 0; i < lastData.record.length; i++) {
                incorporateNewBuffer(ByteUtils.deleteInterval(lastData.record, i, i + 1));
            }
            for (int i = 0; i < lastData.record.length; i++) {
                if (lastData.record[i] == 0) {
                    final byte[] buf = lastData.record.clone();
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
            for (int i = 0; i < lastData.record.length; i++) {
                buckets.get(ByteUtils.unsigned(lastData.record[i])).add(i);
            }
			for (final List<Integer> bucket : buckets) {
				for (final int j : bucket) {
					for (final int k : bucket) {
						if (j < k) {
                            final byte[] buf = lastData.record.clone();
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
            for (int i = 0; i < lastData.record.length; i++) {
                if (lastData.record[i] == 0)
                    continue;
                for (int j = i + 1; j < lastData.record.length; j++) {
                    if (ByteUtils.unsigned(lastData.record[i]) > ByteUtils.unsigned(lastData.record[j])) {
                        incorporateNewBuffer(ByteUtils.swap(lastData.record, i, j));
                    }
                    if (lastData.record[i] != 0 && lastData.record[j] != 0) {
                        final byte[] buf = lastData.record.clone();
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
		return data.getStatus() != Status.INTERESTING || lastData.compareTo(data) > 0;
	}

	boolean incorporateNewBuffer(byte[] buffer) {
		assert buffer.length <= settings.getBufferSize();
        if (Arrays.equals(buffer, lastData.record))
            return false;
        final TestData data = new TestDataForBuffer(buffer);
        testFunction(data);
		if (considerNewTestData(data)) {
			if (lastData.getStatus() == Status.INTERESTING) {
				shrinks++;
			}
			changed++;
			lastData = data;
			if (settings.isDebug())
			if (shrinks >= settings.getMaxShrinks())
				throw new StopShrinking();
			return true;
		}
		return false;
	}

	private byte[] mutateDataToNewBuffer() {
        final int n = Math.min(lastData.record.length, lastData.index());
        if (n == 0) {
			return new byte[0];
		}
		if (n == 1) {
			final byte[] result = new byte[1];
			random.nextBytes(result);
			return result;
		}
        final byte[] result = lastData.record.clone();
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
                u = random.nextInt(lastData.record.length);
                v = u + random.nextInt(lastData.record.length - u);
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
            System.arraycopy(lastData.record, int2.start, result, int1.start, int2.length());
            if (int1.length() != int2.length()) {
                System.arraycopy(lastData.record, int1.end, result, int1.start + int2.length(),
                        lastData.record.length - int1.end);
            }
		}
		return result;
	}

	void newBuffer() {
		final byte[] buffer = new byte[settings.getBufferSize()];
		random.nextBytes(buffer);
        final TestData data = new TestDataForBuffer(buffer);
        testFunction(data);
		data.freeze();
		lastData = data;
	}

	void run() {
		try {
			_run();
		} catch (final StopShrinking ignored) {

		}
	}

	private void testFunction(TestData data) {
		try {
			_testFunction.runTest(data);
		} catch (final StopTest ignored) {
		}
		data.freeze();
		if (data.status.compareTo(Status.VALID) >= 0)
			validExamples++;
    }

    static class StopShrinking extends RuntimeException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

	}
}
