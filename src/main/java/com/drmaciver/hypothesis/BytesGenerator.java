package com.drmaciver.hypothesis;

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
