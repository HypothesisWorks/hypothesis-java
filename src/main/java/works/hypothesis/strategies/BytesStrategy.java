package works.hypothesis.strategies;

import works.hypothesis.TestData;

public class BytesStrategy implements Strategy<byte[]> {
	private final int n;

	public BytesStrategy(int n) {
		super();
		this.n = n;
	}

	public byte[] doDraw(TestData data) {
		return data.drawBytes(n);
	}

}
