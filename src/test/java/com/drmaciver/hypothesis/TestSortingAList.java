package com.drmaciver.hypothesis;

import org.junit.Rule;
import org.junit.Test;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.drmaciver.hypothesis.generators.Generators.integers;
import static com.drmaciver.hypothesis.generators.Generators.lists;
import static org.junit.Assert.assertTrue;

public class TestSortingAList {
    @Rule
    public final TestDataRule data = new TestDataRule();

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

    @Test
    public void testIsSortedAfterSorting(){
        List<Integer> ls = data.draw(lists(integers()));
        ls.sort(Comparator.naturalOrder());
        assertSorted(ls);
    }
}
