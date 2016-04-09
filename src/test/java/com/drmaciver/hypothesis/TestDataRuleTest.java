package com.drmaciver.hypothesis;

import org.junit.Assert;
import org.junit.Rule;

import java.util.List;

import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

public class TestDataRuleTest {
  private static final DataGenerator<Integer> INTEGER = new IntegerGenerator();
	private static final DataGenerator<List<Integer>> INTEGERS = new ListGenerator<Integer>(INTEGER);
	
	@Rule
	public final TestDataRule data = new TestDataRule();

	@Test
	public void testAssociatve() {
		int x = data.draw(INTEGER);
		int y = data.draw(INTEGER);
		int z = data.draw(INTEGER);
  
		Assert.assertEquals((x + y) + z, x + (y + z));
	}
}
