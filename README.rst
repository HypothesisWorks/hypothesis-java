=========================================
Hypothesis for Java feasibility prototype
=========================================

`Hypothesis  <http://hypothesis.readthedocs.org/en/latest/>`_ is a modern property based testing system designed for
mainstream languages. The original version is for Python, where it works extremely well.

This is a very rough prototype of what Hypothesis could look like in Java. It's not even close to being production
ready software. It implements the core algorithm and a very crude API. It has only been lightly tested. It exists to
prove that the concept is feasible, and it achieves that.

Things that it currently supports:

1. The core Hypothesis engine with example generation and shrinking.
2. Working JUnit integration.
3. A very small prototype data generator library.

Things that are currently unimplemented:

1. The example database. The model used is the Hypothesis one where data is represented in an easy to serialize format,
   so implementing this is a Simple Matter Of Code, but it doesn't affect the feasibility so I haven't bothered yet.
2. The example mutator and shrinker are significantly less sophisticated than the Python one. There's no technical
   reason for this to be case, they're just based on an earlier prototype of the concept that works well enough for a
   tech demo.
3. Most of the data generation library. This is in many ways where the bulk of the work in porting an implementation
   lies.
4. Decent example printing. Right now it just uses toString, which is not ideal. Additionally it's not obvious what
   the best way to hook printing into JUnit is.
5. A license that you would want to use it under.

Example usage
-------------

The Hypothesis Java prototype is build on JUnit's `Rules <https://github.com/junit-team/junit4/wiki/Rules>`_
functionality.

You use a TestDataRule as a source of data. Within a test you can then call draw on it with a strategy
argument to get an example drawn from that strategy. You can call draw as many times as you want for
tests that need multiple values.

Here's an example of testing a sorting function using this:

.. code-block:: java

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

      @Test
      public void testIsSortedAfterSorting(){
          List<Integer> ls = data.draw(lists(integers()));
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


Licensing
---------

This is currently under the AGPL. That is not intended as a long term licensing strategy but gives me more
flexibility for future plans. The most likely future licensing path is that there will be a core version available
under the MPLv2 or Apache, with a proprietary extension library containing most of the strategies and some useful
extensions. I could be persuaded to make the whole thing open source under Apache/MPLv2 but the persuasion would have
to be financial.

Future Plans
------------

I'm not very interested in supporting another fully fledged property based testing implementation of the calibre
of the Python version of Hypothesis for free. As such this library is likely to remain a prototype without some
financial backing to change that, in the form of paid development and/or ongoing support contracts.

If you are interested in a fully fledged Hypothesis for Java, please `contact  me <mailto:david@drmaciver.com>`_ to
discuss terms.
