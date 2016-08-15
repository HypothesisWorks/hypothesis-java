package works.hypothesis;

import works.hypothesis.strategies.Strategy;

public class Hypothesis {
	public static <T> T find(Strategy<T> generator, HypothesisPredicate<T> condition) throws NoSuchExample {
		return Hypothesis.find(generator, condition, null);
	}

	public static <T> T find(final Strategy<T> generator, final HypothesisPredicate<T> condition,
							 final HypothesisSettings settings) throws NoSuchExample {
		final byte[] buffer = TestRunner.findInterestingBuffer(new HypothesisTestFunction() {
			public void runTest(TestData data) {
				final T value = generator.doDraw(data);
                if (condition.test(value)) {
                    data.markInteresting();
				}
			}
		}, settings);
		if (buffer == null) {
			throw new NoSuchExample();
		} else {
            try {
                final T result = generator.doDraw(new TestDataForBuffer(buffer));
                if (!condition.test(result)) {
                throw new Flaky("Result " + result + " did not satisfy condition.");
                }
			return result;
            } catch (StopTest e) {
                throw new Flaky("Generator drew a different amount of data on retry");
            }
        }
	}
}
