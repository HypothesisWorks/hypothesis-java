package works.hypothesis;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by david on 4/9/16.
 */
public class TestTestData {

    @Test
    public void testRecordsBufferDraw() {
        byte[] buffer = new byte[256];
        for (int i = 0; i < buffer.length; i++) buffer[i] = (byte) i;
        TestData data = new TestDataForBuffer(buffer);
        Assert.assertArrayEquals(new byte[]{0}, data.drawBytes(1));
        Assert.assertArrayEquals(new byte[]{1, 2}, data.drawBytes(2));
        Assert.assertArrayEquals(new byte[]{3, 4, 5}, data.drawBytes(3));
        Assert.assertEquals(6, data.index);
        data.freeze();
        Assert.assertArrayEquals(new byte[]{0, 1, 2, 3, 4, 5}, data.record);
    }

}
