package com.drmaciver.hypothesis;

import java.util.Random;

/**
 * Created by david on 4/9/16.
 */
public class DistributionalTestData extends TestData {
    private final Random random;

    public DistributionalTestData(int maxSize, Random random) {
        super(maxSize);
        this.random = random;
    }

    @Override
    protected byte[] doDrawBytes(int n, HypothesisDataDistribution distribution) {
        byte[] result = new byte[n];
        distribution.generateData(random, result, 0, n);
        return result;
    }
}
