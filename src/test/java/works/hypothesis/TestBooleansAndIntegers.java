package works.hypothesis;

import org.junit.Rule;
import org.junit.Test;

import static works.hypothesis.strategies.Strategies.booleans;
import static works.hypothesis.strategies.Strategies.integers;
import static junit.framework.TestCase.assertFalse;

public class TestBooleansAndIntegers {
    @Rule
    public final TestDataRule data = new TestDataRule();

    @Test
    public void testBooleansAreNotIntegers(){
        Integer anInt = data.draw(integers());
        Boolean aBool = data.draw(booleans());
        assertFalse(anInt.equals(aBool));
    }
}
