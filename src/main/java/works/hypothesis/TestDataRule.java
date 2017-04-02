package works.hypothesis;

import works.hypothesis.strategies.Strategy;
import org.junit.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.logging.Logger;

public class TestDataRule extends Object implements TestRule {
    private final HypothesisSettings settings;

    private TestData data = null;

    private Throwable lastError = null;

    private Logger logger;
    private int index = 0;

    public TestDataRule() {
        this(new HypothesisSettings());
    }

    public TestDataRule(HypothesisSettings settings) {
        super();
        this.settings = settings;
    }

    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                logger = null;
                index = 0;
                final TestRunner runner = new TestRunner(new HypothesisTestFunction() {
                    public void runTest(TestData d) {
                        data = d;
                        try {
                            base.evaluate();
                        } catch (final AssumptionViolatedException e) {
                            d.markInvalid();
                        } catch (final HypothesisException e) {
                            throw e;
                        } catch (final Throwable t) {
                            if (!d.isFrozen()) {
                                lastError = t;
                                d.markInteresting();
                            }
                        }
                    }
                }, settings);
                runner.run();
                if (runner.lastData.getStatus() == Status.INTERESTING) {
                    logger = Logger
                            .getLogger(description.getTestClass().getName() + '.' + description.getDisplayName());
                    index = 0;
                    assert lastError != null;
                    data = new TestDataForBuffer(runner.lastData.record);
                    base.evaluate();
                    throw new Flaky("Expected error: " + lastError.toString());
                }
            }
        };
    }

    public <T> T draw(Strategy<T> generator) {
        final T result = getData().draw(generator);
        final String string = result.toString();
        if (logger != null) {
            logger.info("Draw #" + (++index) + ": " + string);
        }
        return result;
    }

    TestData getData() {
        assert data != null;
        return data;
    }
}
