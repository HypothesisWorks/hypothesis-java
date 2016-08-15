package works.hypothesis;

import java.util.Random;

/**
 * Created by david on 4/9/16.
 */
public interface HypothesisDataDistribution {
    void generateData(Random random, byte[] target, int index, int nbytes);


}
