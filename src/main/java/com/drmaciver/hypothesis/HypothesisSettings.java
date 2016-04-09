package com.drmaciver.hypothesis;

public class HypothesisSettings {
	private int bufferSize = 8 * 1024;

	private int mutations = 50;

	private int maxExamples = 200;

	private int maxShrinks = 2000;

	private boolean debug = false;

	public int getBufferSize() {
		return bufferSize;
	}

	public int getMaxExamples() {
		return maxExamples;
	}

	public int getMaxShrinks() {
		return maxShrinks;
	}

	public int getMutations() {
		return mutations;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setMaxExamples(int maxExamples) {
		this.maxExamples = maxExamples;
	}

	public void setMaxShrinks(int maxShrinks) {
		this.maxShrinks = maxShrinks;
	}

	public void setMutations(int mutations) {
		this.mutations = mutations;
	}

}
