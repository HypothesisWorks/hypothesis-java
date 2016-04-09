package com.drmaciver.hypothesis;

import com.drmaciver.hypothesis.generators.BytesGenerator;
import com.drmaciver.hypothesis.generators.DataGenerator;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by david on 4/9/16.
 */
public class TestByteUtils {
    @Rule
    public final TestDataRule data = new TestDataRule();

    public final DataGenerator<byte[]> INTBYTES = new BytesGenerator(4);

    @Test
    public void testBidirectionalConversion() {
        byte[] bytes = data.draw(INTBYTES);
        int i = ByteUtils.intFromBytes(bytes, 0);
        byte[] rebytes = new byte[4];
        ByteUtils.intToBytes(rebytes, 0, i);
        assertArrayEquals(bytes, rebytes);
    }
}
