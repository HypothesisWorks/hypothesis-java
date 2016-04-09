package com.drmaciver.hypothesis;

import java.util.Arrays;

public class TestDataForBuffer extends TestData {

    private final byte[] buffer;

    public TestDataForBuffer(byte[] buffer) {
        super(buffer.length, buffer);
        this.buffer = buffer.clone();
    }

    @Override
    protected byte[] doDrawBytes(int n) {
        return Arrays.copyOfRange(buffer, index(), index() + n);
    }

}
