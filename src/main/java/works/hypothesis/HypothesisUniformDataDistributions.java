package works.hypothesis;

import java.util.Random;

/**
 * Created by david on 4/9/16.
 */
public class HypothesisUniformDataDistributions implements HypothesisDataDistribution {
    public static final HypothesisUniformDataDistributions INSTANCE = new HypothesisUniformDataDistributions();

    @Override
    public void generateData(Random random, byte[] target, int index, int nbytes) {
        for (int j = 0; j < nbytes; j++) {
            target[index + j] = (byte) random.nextInt(256);
        }
    }
}
