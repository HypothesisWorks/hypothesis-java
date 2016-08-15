package works.hypothesis;

import works.hypothesis.strategies.BytesStrategy;
import works.hypothesis.strategies.Strategy;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by david on 4/9/16.
 */
public class TestByteUtils {
    @Rule
    public final TestDataRule data = new TestDataRule();

    public final Strategy<byte[]> INTBYTES = new BytesStrategy(4);

    @Test
    public void testBidirectionalConversion() {
        byte[] bytes = data.draw(INTBYTES);
        int i = ByteUtils.intFromBytes(bytes, 0);
        byte[] rebytes = new byte[4];
        ByteUtils.intToBytes(rebytes, 0, i);
        assertArrayEquals(bytes, rebytes);
    }
}
