package com.drmaciver.hypothesis;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestExampleQuality {

	static int sum(List<Integer> t) {
		int c = 0;
		for (final int i : t)
			c += i;
		return c;
	}

	@Test
	public void testLexicographicBytes() throws NoSuchExample {
		final byte[] data = Hypothesis.find(new BytesGenerator(1000), new HypothesisPredicate<byte[]>() {
			public boolean test(byte[] t) {
				int c = 0;
				for (final byte b : t) {
					if (b > 0)
						c++;
				}
				return c >= 100;
			}
		});
		assertEquals(1000, data.length);
		for (int i = 0; i < 1000; i++) {
			if (i < 900)
				assertEquals(0, data[i]);
			else
				assertEquals(1, data[i]);
		}

	}

	@Test
	public void testSummingIntegers() throws NoSuchExample {
		final int n = 1000000;
		final HypothesisSettings settings = new HypothesisSettings();
		settings.setDebug(true);
		final List<Integer> result = Hypothesis.find(new ListGenerator<Integer>(new IntegerGenerator()),
				new HypothesisPredicate<List<Integer>>() {
					public boolean test(List<Integer> t) {
						return sum(t) >= n;
					}
				}, settings);
		assertEquals(n, sum(result));
	}
}
