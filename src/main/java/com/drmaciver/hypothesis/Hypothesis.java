package com.drmaciver.hypothesis;

public class Hypothesis {
	public static <T> T find(DataGenerator<T> generator, HypothesisPredicate<T> condition) throws NoSuchExample {
		return Hypothesis.find(generator, condition, null);
	}

	public static <T> T find(final DataGenerator<T> generator, final HypothesisPredicate<T> condition,
			final HypothesisSettings settings) throws NoSuchExample {
		final byte[] buffer = TestRunner.findInterestingBuffer(new HypothesisTestFunction() {
			public void runTest(TestData data) {
				final T value = generator.doDraw(data);
				if (condition.test(value)) {
					// data.incurCost(value.toString().length());
					data.markInteresting();
				}
			}
		}, settings);
		if (buffer == null) {
			throw new NoSuchExample();
		} else {
			final T result = generator.doDraw(new TestData(buffer));
			if (!condition.test(result)) {
				throw new Flaky("Result did not satisfy condition.");
			}
			return result;
		}
	}
}
