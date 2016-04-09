package com.drmaciver.hypothesis;

/**
 * Created by david on 4/9/16.
 */
class Frozen extends HypothesisException {
    /**
     * A mutation method has been called on a frozen TestDataForBuffer.
     */
    private static final long serialVersionUID = 1L;

    public Frozen(String arg0) {
        super(arg0);
    }
}
