package com.drmaciver.hypothesis;

public class ByteUtils {
	static byte[] deleteInterval(byte[] x, int start, int end) {
		final byte[] result = new byte[x.length - (end - start)];
		System.arraycopy(x, 0, result, 0, start);
		System.arraycopy(x, end, result, start, x.length - end);
		return result;
	}

	static byte[] sortInterval(byte[] x, int start, int end) {
		assert start <= end;
		assert end <= x.length;
		final byte[] result = x.clone();
		final int[] counter = new int[256];
		for (int i = start; i < end; i++) {
			final int b = unsigned(x[i]);
			counter[b]++;
		}
		int index = start;
		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < counter[i]; j++)
				result[index++] = (byte) i;
		}
		return result;
	}

	public static byte[] swap(byte[] buffer, int i, int j) {
		final byte[] result = buffer.clone();
		final byte c = result[i];
		result[i] = result[j];
		result[j] = c;
		return result;
	}

	static int unsigned(byte b) {
		return b & 0xff;
	}

	static byte[] zeroInterval(byte[] x, int start, int end) {
		final byte[] result = x.clone();
		for (int i = start; i < end; i++)
			result[i] = (byte) 0;
		return result;
	}
}
