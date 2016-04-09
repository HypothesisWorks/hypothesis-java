package com.drmaciver.hypothesis;

public class HypothesisException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public HypothesisException() {
		super();
	}

	public HypothesisException(String arg0) {
		super(arg0);
	}

	public HypothesisException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public HypothesisException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public HypothesisException(Throwable arg0) {
		super(arg0);
	}

}
