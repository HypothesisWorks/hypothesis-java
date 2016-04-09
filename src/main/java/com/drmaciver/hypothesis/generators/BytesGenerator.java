package com.drmaciver.hypothesis.generators;

import com.drmaciver.hypothesis.TestData;

public class BytesGenerator implements DataGenerator<byte[]> {
	private final int n;

	public BytesGenerator(int n) {
		super();
		this.n = n;
	}

	public byte[] doDraw(TestData data) {
		return data.drawBytes(n);
	}

}
