package works.hypothesis;

public interface HypothesisPredicate<T> {
    boolean test(T value);
}
