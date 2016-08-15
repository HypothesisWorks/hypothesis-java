package works.hypothesis.strategies;

/**
 * Created by david on 4/18/16.
 */
public interface Strategies {
    static <T> ListStrategy<T> lists(Strategy<T> elementGenerator) {
        return new ListStrategy<T>(elementGenerator);
    }

    static Strategy<Integer> integers() {
        return new IntegerStrategy();
    }
    static Strategy<Boolean> booleans() { return new BooleanStrategy(); }
}
