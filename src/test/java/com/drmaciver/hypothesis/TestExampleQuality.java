package com.drmaciver.hypothesis;

import com.drmaciver.hypothesis.generators.BytesGenerator;
import com.drmaciver.hypothesis.generators.IntegerGenerator;
import com.drmaciver.hypothesis.generators.ListGenerator;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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


    @Test
    public void testFloatShrinksTowardsZero() throws NoSuchExample {
        float result = Hypothesis.find(new FloatRangeGenerator(0, 1), new HypothesisPredicate<Float>() {
            @Override
            public boolean test(Float value) {
                return value > 0;
            }
        });
        assertEquals(0.0, result, 0.01);
    }


    @Test
    public void testNonAssociativeFloats() throws NoSuchExample {
        final HypothesisSettings settings = new HypothesisSettings();
        settings.setMaxExamples(10000);

        final List<Float> result = Hypothesis.find(new ListGenerator<Float>(new FloatRangeGenerator(0, 1)),
                new HypothesisPredicate<List<Float>>() {
                    public boolean test(List<Float> t) {
                        if (t.size() < 3) return false;
                        float x = t.get(0);
                        float y = t.get(1);
                        float z = t.get(2);
                        float u = (x + y) + z;
                        float v = x + (y + z);
                        return u != v;
                    }
                }, settings);
        assertEquals(3, result.size());
        assertTrue(result.get(0) + result.get(1) + result.get(2) < 1);

    }
}
