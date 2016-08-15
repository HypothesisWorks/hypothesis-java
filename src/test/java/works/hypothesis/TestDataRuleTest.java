package works.hypothesis;

import works.hypothesis.strategies.Strategy;
import works.hypothesis.strategies.Strategies;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

public class TestDataRuleTest {
  private static final Strategy<Integer> INTEGER = Strategies.integers();
	private static final Strategy<List<Integer>> INTEGERS = Strategies.lists(INTEGER);
	
	@Rule
	public final TestDataRule data = new TestDataRule();

	@Test
	public void testAssociative3() {
		int x = data.draw(INTEGER);
		int y = data.draw(INTEGER);
		int z = data.draw(INTEGER);
  
		Assert.assertEquals((x + y) + z, x + (y + z));
	}


	@Test
	public void testReversibleLists() {
		List<Integer> xs = data.draw(INTEGERS);
		int sumLeft = 0;
		int sumRight = 0;
		for(int i = 0; i < xs.size(); i++){
			sumLeft += xs.get(i);
			sumRight += xs.get(xs.size() - i - 1);
		}
		Assert.assertEquals(sumLeft, sumRight);
	}
}
