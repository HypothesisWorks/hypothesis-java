package works.hypothesis;

public class Flaky extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public Flaky(String string) {
		super(string);
	}

}
