package com.drmaciver.hypothesis;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class TestExampleQuality {

	static int sum(List<Integer> t) {
		int c = 0;
		for (final int i : t)
			c += i;
		return c;
	}

	@Test
	public void testLexicogaphicBytes() throws NoSuchExample {
		final byte[] data = Conjecture.find(new BytesGenerator(1000), new ConjecturePredicate<byte[]>() {
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
		final ConjectureSettings settings = new ConjectureSettings();
		settings.setDebug(true);
		final List<Integer> result = Conjecture.find(new ListGenerator<Integer>(new IntegerGenerator()),
				new ConjecturePredicate<List<Integer>>() {
					public boolean test(List<Integer> t) {
						return sum(t) >= n;
					}
				}, settings);
		System.out.println(result.toString());
		assertEquals(n, sum(result));
	}
}
