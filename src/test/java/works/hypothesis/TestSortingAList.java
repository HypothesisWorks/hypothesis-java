package works.hypothesis;

import org.junit.Rule;
import org.junit.Test;
import works.hypothesis.strategies.Strategies;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestSortingAList {
    @Rule
    public final TestDataRule data = new TestDataRule();

    @Test
    public void testIsSortedAfterSorting(){
        List<Integer> ls = data.draw(Strategies.lists(Strategies.integers()));
        ls.sort(Comparator.naturalOrder());
        assertSorted(ls);
    }

    // Utility assertion function. Doesn't use any Hypothesis functionality.
    private <T extends Comparable<T>> void assertSorted(List<T> elements){
        if(elements.isEmpty()) return;
        Iterator<T> it = elements.iterator();
        T previous = it.next();
        while(it.hasNext()){
            T current = it.next();
            assertTrue(previous.compareTo(current) <= 0);
            previous = current;
        }
    }

}
