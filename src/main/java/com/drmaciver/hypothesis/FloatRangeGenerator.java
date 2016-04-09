package com.drmaciver.hypothesis;

import com.drmaciver.hypothesis.generators.DataGenerator;

import java.util.Random;

/**
 * Created by david on 4/9/16.
 */
public class FloatRangeGenerator implements DataGenerator<Float> {
    private final float right;
    private final float left;
    private final HypothesisDataDistribution distribution;

    FloatRangeGenerator(float left, float right) {
        this.left = left;
        this.right = right;
        this.distribution = new HypothesisDataDistribution() {

            @Override
            public void generateData(Random random, byte[] target, int index, int nbytes) {
                if (nbytes != 4) {
                    throw new RuntimeException("Bad number of bytes " + nbytes);
                }
                float f = random.nextFloat() * (right - left) + left;
                ByteUtils.intToBytes(target, index, Float.floatToIntBits(f));
            }
        };
    }


    @Override
    public Float doDraw(TestData data) {
        byte[] value = data.drawBytes(4, this.distribution);
        float f = Float.intBitsToFloat(ByteUtils.intFromBytes(value, 0));
        data.assume(f >= left);
        data.assume(f <= right);
        return f;
    }

}
